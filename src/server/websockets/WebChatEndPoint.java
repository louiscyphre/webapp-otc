package server.websockets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import server.AppConstants;
import server.DataManager;
import server.messages.ChannelFailure;
import server.messages.ChannelSuccess;
import server.messages.Discovery;
import server.messages.IncomingMessage;
import server.messages.MessageReceived;
import server.messages.SubscribeFailure;
import server.messages.SubscribeSuccess;
import server.messages.Unsubscribe;
import server.messages.UserSubscribed;
import server.messages.UserUnsubscribed;
import server.model.Channel;
import server.model.ChannelCredentials;
import server.model.ChannelDiscovery;
import server.model.ChannelViewing;
import server.model.Message;
import server.model.MessageCredentials;
import server.model.Subscription;
import server.model.ThreadUser;
import server.model.User;
import server.util.BuildSuccessMessages;

/**
 * Example of a simple Server-side WebSocket end-point that managed a chat between 
 * several client end-points
 * @author haggai
 */
@ServerEndpoint("/{username}")
public class WebChatEndPoint {

	//tracks all active chat users
	private static Map<Session,User> chatUsers = Collections.synchronizedMap(new HashMap<Session,User>());

	/**
	 * Joins a new client to the chat
	 * @param session 
	 * 			client end point session
	 * @throws IOException
	 */
	@OnOpen
	public void joinChat(Session session, @PathParam("username") String username) throws IOException{
		if (session.isOpen()) {
			try {
				Connection conn = getDataBaseConnection();
				if (conn != null) {
					// get user information from database
					User user = DataManager.getUserByUsername(conn, username);
					//add new client to managed chat sessions
					chatUsers.put(session, user);
					System.out.println("the following user logged in: " + user.getUsername());
				}
				conn.close(); // close connection
			} catch (SQLException e) {
				session.close();
			}
		}
	}

	/**
	 * Message delivery between chat participants
	 * @param session
	 * 			client end point session
	 * @param msg
	 * 			message to deliver		
	 * @throws IOException
	 */
	@OnMessage
	public void deliverChatMessege(Session session, String msg) throws IOException{
		try {
			if (session.isOpen()) {
				Connection conn = getDataBaseConnection();
				if (conn != null) {
					// parse the message according to "MessageType" String
					JsonParser jParser = new JsonParser();
					JsonObject jObj = jParser.parse(msg).getAsJsonObject();
					String msgType = jObj.get(AppConstants.MESSAGE_PROPERTY).getAsString(); // parse which type of message received
					JsonObject msgContent = jObj.get(AppConstants.MESSAGE_CONTENT).getAsJsonObject(); // parse which message content received
					// decipher message
					switch (msgType) {
					case AppConstants.MESSAGE_CREATE_CHANNEL: // request to create a channel
						System.out.println("received \"Create Channel\" request");
						handleCreateChannelMessage(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_SUBSCRIBE: // request to subscribe to a channel
						System.out.println("received \"Subscribe\" request");
						handleSubscribeToChannelMessage(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_UNSUBSCRIBE: // request to unsubscribe from a channel
						System.out.println("received \"Unsubscribe\" request");
						handleUnsubscribeToChannelMessage(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_CHANNEL_DISCOVERY: // request to search for channels
						System.out.println("received \"Discovery\" request");
						handleChannelDiscoveryMessage(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_CHANNEL_VIEWING: // update whether the user is viewing a channel
						System.out.println("received \"Channel Viewing\" request");
						handleChannelViewingMessage(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_RECEIVED_MESSAGE: // request to deal with a received message
						System.out.println("received \"Message\" request");
						handleReceivedMessage(session, conn, msgContent);
						break;
					}
				}
				conn.close();
			}
		} catch (IOException | SQLException e) {
			session.close();
		}
	}

	/**
	 * Removes a client from the chat
	 * @param session
	 * 			client end point session
	 */
	@OnClose
	public void leaveChat(Session session) {
		chatUsers.remove(session); // fake user just for removal
	}
	
	/**
	 * Notifies a user about a message
	 * @param user the user to send the message to
	 * @param message the message to be sent
	 * @throws IOException
	 */
	private void doNotify(User user, String message) throws IOException {
		for (Entry<Session, User> userEntry : chatUsers.entrySet()) {
			Session session = userEntry.getKey();
			if (userEntry.getValue().getUsername().equals(user.getUsername()) && session.isOpen()) {
				session.getBasicRemote().sendText(message);
			}
		}
	}

	/**
	 * Notifies a user about a message
	 * @param user the user to send the message to
	 * @param message the message to be sent
	 * @throws IOException
	 */
	private void doNotify(ThreadUser user, String message) throws IOException {
		for (Entry<Session, User> userEntry : chatUsers.entrySet()) {
			Session session = userEntry.getKey();
			if (userEntry.getValue().getUsername().equals(user.getUsername()) && session.isOpen()) {
				session.getBasicRemote().sendText(message);
			}
		}
	}
	
	/**
	 * Notifies all the users in a channel about a message
	 * @param channel the channel which users to update
	 * @param message the message to be sent
	 * @param skip the session to which not to sent the message (to skip)
	 * @throws IOException
	 */
	private void doNotifyByChannel(Channel channel, String message, Session skip) throws IOException {
		for (Entry<Session, User> userEntry : chatUsers.entrySet()) {
			Session session = userEntry.getKey();
			if (session.isOpen() && (skip == null || !session.equals(skip))) {
				for (ThreadUser channelUser : channel.getUsers()) {
					if (userEntry.getValue().getUsername().equals(channelUser.getUsername())) {
						session.getBasicRemote().sendText(message);
					}
				}
			}
		}
	}

	private Connection getDataBaseConnection() {
		// obtain CustomerDB data source from Tomcat's context
		Context context;
		try {
			context = new InitialContext();
			BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_CONTEXT + AppConstants.OPEN);
			Connection conn = ds.getConnection();
			return conn;
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void handleCreateChannelMessage(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		ChannelCredentials credentials = gson.fromJson(msgContent, ChannelCredentials.class);
		if (DataManager.getChannelByName(conn, credentials.getName()) == null) { // channel with this name does not exist yet
			Channel channel = DataManager.addChannel(conn, credentials); // create channel
			DataManager.addSubscription(conn, new Subscription(credentials.getName(), chatUsers.get(session).getUsername()), new Timestamp(System.currentTimeMillis())); // subscribe the creator to the channel
			if (credentials.getUsername() != null) { // private channel
				DataManager.addSubscription(conn, new Subscription(credentials.getName(), credentials.getUsername()), new Timestamp(System.currentTimeMillis()));
				User secondUser = DataManager.getUserByUsername(conn, credentials.getUsername());
				channel.setChannelName(secondUser.getNickname());
				session.getBasicRemote().sendText(gson.toJson(channel));
				doNotify(secondUser, gson.toJson(BuildSuccessMessages.buidSubscribeSuccess(conn, new Channel(
						chatUsers.get(session).getUsername(), // the name of the channel is the creator's name
						credentials.getDescription(), // the description of the channel
						2, // the creator of the channel and this user => 2
						false)))); // it's a private channel (i.e. *not* public)
			} else { // public channel
				session.getBasicRemote().sendText(gson.toJson(new ChannelSuccess(channel)));
			}
		} else { // channel exists
			session.getBasicRemote().sendText(gson.toJson(new ChannelFailure(credentials.getName(), "Channel already exists")));
		}
	}

	private void handleSubscribeToChannelMessage(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		Subscription credentials = gson.fromJson(msgContent, Subscription.class);
		Channel channel = null;
		credentials.setUsername(chatUsers.get(session).getUsername());
		if ((channel = DataManager.getChannelByName(conn, credentials.getChannelName())) != null) { // check if channel exists
			if (channel.isPublic()) {
				if (DataManager.getSubscriptionByChannelAndUsername(conn, channel.getChannelName(), credentials.getUsername()) == null) { // if unsubscribed
					DataManager.addSubscription(conn, credentials, new Timestamp(System.currentTimeMillis()));
					channel.setNumberOfSubscribers(channel.getNumberOfSubscribers() + 1);
					DataManager.updateChannel(conn, channel);
					SubscribeSuccess subscribeSucces = BuildSuccessMessages.buidSubscribeSuccess(conn, channel);
					if (subscribeSucces != null) {
						session.getBasicRemote().sendText(gson.toJson(subscribeSucces));
						// notify the all the users in this channel of the new subscription
						doNotifyByChannel(
								channel,
								gson.toJson(new UserSubscribed(
										channel.getChannelName(),
										ThreadUser.getThreadUserByUser(DataManager.getUserByUsername(conn, credentials.getUsername())))),
								session); // unsubscribe the user on all other clients
					} else {
						session.getBasicRemote().sendText(gson.toJson(new SubscribeFailure("General error")));
					}
				} else {
					session.getBasicRemote().sendText(gson.toJson(new SubscribeFailure("Already subscribed to channel")));
				}
			} else {
				session.getBasicRemote().sendText(gson.toJson(new SubscribeFailure("Cannot subscribe to a private channel")));
			}
		} else { // channel doesn't exist
			session.getBasicRemote().sendText(gson.toJson(new SubscribeFailure("Channel does not exist")));
		}
	}

	private void handleUnsubscribeToChannelMessage(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		Subscription credentials = gson.fromJson(msgContent, Subscription.class);
		Channel channel = null;
		credentials.setUsername(chatUsers.get(session).getUsername());
		if ((channel = DataManager.getChannelByName(conn, credentials.getChannelName())) != null) { // check if channel exists
			if (channel.isPublic()) {
				if (DataManager.getSubscriptionByChannelAndUsername(conn, credentials.getChannelName(), credentials.getUsername()) != null) { // if subscribed
					DataManager.removeSubscription(conn, credentials);
					channel.setNumberOfSubscribers(channel.getNumberOfSubscribers() - 1);
					DataManager.updateChannel(conn, channel);
					DataManager.updateChannelUsers(conn, channel);
					session.getBasicRemote().sendText(gson.toJson(new Unsubscribe(credentials.getChannelName())));
					// update the remaining users in the channel that the user has quit
					doNotifyByChannel(
							channel,
							gson.toJson(new UserUnsubscribed(channel.getChannelName(), credentials.getUsername())),
							session); // unsubscribe the user on all other clients
				} else {
					session.getBasicRemote().sendText(gson.toJson(new Unsubscribe("Not subscribed to channel")));
				}
			} else {
				session.getBasicRemote().sendText(gson.toJson(new Unsubscribe("Cannot subscribe to a private channel")));
			}
		} else { // channel doesn't exist
			session.getBasicRemote().sendText(gson.toJson(new Unsubscribe("Channel does not exist")));
		}
	}
	
	private void handleChannelDiscoveryMessage(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		ChannelDiscovery credentials = gson.fromJson(msgContent, ChannelDiscovery.class);
		session.getBasicRemote().sendText(gson.toJson(new Discovery(DataManager.discoverChannels(conn, credentials))));
	}
	
	private void handleChannelViewingMessage(Session session, Connection conn, JsonObject msgContent) {
		Gson gson = new Gson();
		ChannelViewing credentials = gson.fromJson(msgContent, ChannelViewing.class);
		Subscription subscription = DataManager.getSubscriptionByChannelAndUsername(conn, credentials.getChannel(), chatUsers.get(session).getUsername());
		if (subscription != null) {
			subscription.setViewing(credentials.isViewing());
			if (subscription.isViewing()) {
				subscription.setUnreadMessages(0);
				subscription.setUnreadMentionedMessages(0);
			}
			DataManager.updateSubscription(conn, subscription);
		}
	}
	
	private void handleReceivedMessage(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		MessageCredentials credentials = gson.fromJson(msgContent, MessageCredentials.class);
		
		if (DataManager.getChannelByName(conn, credentials.getChannel()) != null) { // check if channel exists
			User user = null;
			if ((user = DataManager.getUserByUsername(conn, credentials.getUsername())) != null) { // check if user exists
				if (DataManager.getSubscriptionByChannelAndUsername(conn, credentials.getChannel(), credentials.getUsername()) != null) { // check if subscribed
					Channel channel = DataManager.getChannelByName(conn, credentials.getChannel()); // get the required channel
					DataManager.updateChannelUsers(conn, channel); // get the channel's users
					Message message = DataManager.addMessage(conn, credentials, new ThreadUser(user.getUsername(), user.getNickname(), user.getDescription(), user.getAvatarUrl())); // parse the message
					session.getBasicRemote().sendText(gson.toJson(new MessageReceived())); // update the sender that his message was receeived
					for (ThreadUser thUser : channel.getUsers()) {
						Subscription subscription = DataManager.getSubscriptionByChannelAndUsername(conn, channel.getChannelName(), thUser.getUsername());
						if (!subscription.isViewing()) { // if user is not viewing the channel right now
							subscription.setUnreadMessages(subscription.getUnreadMessages() + 1); // mark as unread
							if (message.getContent().contains("@" + thUser.getUsername())) { // check if current user was mentioned
								subscription.setUnreadMentionedMessages(subscription.getUnreadMentionedMessages() + 1); // update that there is an unread mention
							}
						}
						doNotify(thUser, gson.toJson(new IncomingMessage(message, subscription.getUnreadMessages(), subscription.getUnreadMentionedMessages()))); // update all users in chat about the new message
					}
				} else {
					session.getBasicRemote().sendText(gson.toJson(new MessageReceived("Not subscribed to channel")));
				}
			} else {
				session.getBasicRemote().sendText(gson.toJson(new MessageReceived("User does not exist")));
			}
		} else { // channel doesn't exist
			session.getBasicRemote().sendText(gson.toJson(new MessageReceived("Channel does not exist")));
		}
	}
}
