package server.websockets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
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
import server.messages.DownloadMessages;
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
import server.model.MessageDownload;
import server.model.MessageThread;
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
	private static Map<Session, User> chatUsers = Collections.synchronizedMap(new HashMap<Session, User>());
	private static Map<Session, Map<String, Map<Integer, MessageThread>>> downloadedMessages = Collections.synchronizedMap(new HashMap<Session, Map<String, Map<Integer, MessageThread>>>());
	private static Map<Session, Channel> chatViewedChannels = Collections.synchronizedMap(new HashMap<Session, Channel>());

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
					User user = DataManager.getUserByUsername(conn, username); // get user information from database
					if (user != null) {
						chatUsers.put(session, user); // add new client to managed chat sessions
						System.out.println("the following user logged in: " + user.getUsername());
					}
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
	 */
	@OnMessage
	public void deliverChatMessege(Session session, String msg) {
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
						handleCreateChannelRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_SUBSCRIBE: // request to subscribe to a channel
						System.out.println("received \"Subscribe\" request");
						handleSubscribeToChannelRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_UNSUBSCRIBE: // request to unsubscribe from a channel
						System.out.println("received \"Unsubscribe\" request");
						handleUnsubscribeToChannelRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_CHANNEL_DISCOVERY: // request to search for channels
						System.out.println("received \"Discovery\" request");
						handleChannelDiscoveryRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_CHANNEL_VIEWING: // notified that user is currently viewing this channel
						System.out.println("received \"Channel Viewing\" notification");
						handleChannelViewingRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_DOWNLOAD_MESSAGES: // request to download 10 messages of a chat
						System.out.println("received \"Download Messages\" request");
						handleDownloadMessagesRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_RECEIVED_MESSAGE: // request to deal with a received message
						System.out.println("received \"Message\" request");
						handleReceivedMessageRequest(session, conn, msgContent);
						break;
					default:
						System.out.println("received \"" + msgType + "\"");
					}
					conn.close();
				}
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes a client from the chat
	 * @param session
	 * 			client end point session
	 */
	@OnClose
	public void leaveChat(Session session) {
		chatUsers.remove(session);
		downloadedMessages.remove(session);
		chatViewedChannels.remove(session);
	}
	
	@OnError
	public void error(Session session, Throwable t) {
	}
	
	/**
	 * Notifies a user about a message
	 * @param session
	 * 			the session in which to send message
	 * @param message
	 * 			the message to be sent
	 * @throws IOException
	 */
	private void doNotify(Session session, String message) throws IOException {
		if (session.isOpen()) {
			session.getBasicRemote().sendText(message);
		}
	}
	
	/**
	 * Notifies a user about a message
	 * @param user
	 * 			the user to send the message to
	 * @param message
	 * 			the message to be sent
	 * @throws IOException
	 */
	private void doNotify(User user, String message) throws IOException {
		for (Entry<Session, User> userEntry : chatUsers.entrySet()) {
			Session session = userEntry.getKey();
			if (session.isOpen() && userEntry.getValue().getUsername().equals(user.getUsername())) {
				session.getBasicRemote().sendText(message);
			}
		}
	}

	/**
	 * Notifies a user about a message
	 * @param user
	 * 			the user to send the message to
	 * @param message
	 * 			the message to be sent
	 * @throws IOException
	 */
	private void doNotify(ThreadUser user, String message) throws IOException {
		for (Entry<Session, User> userEntry : chatUsers.entrySet()) {
			Session session = userEntry.getKey();
			if (session.isOpen() && userEntry.getValue().getUsername().equals(user.getUsername())) {
				session.getBasicRemote().sendText(message);
			}
		}
	}
	
	/**
	 * Notifies all the users in a channel about a message
	 * @param channel
	 * 			the channel which users to update
	 * @param message
	 * 			the message to be sent
	 * @param skip
	 * 			the session to which not to sent the message (to skip)
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
		try {
			Context context = new InitialContext(); // obtain CustomerDB data source from Tomcat's context
			BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_CONTEXT + AppConstants.OPEN);
			return ds.getConnection();
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void handleCreateChannelRequest(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		ChannelCredentials credentials = gson.fromJson(msgContent, ChannelCredentials.class);
		String channelName;
		if (credentials.getUsername() != null) { // private channel
			String currentUser = chatUsers.get(session).getUsername();
			String secondUser = credentials.getUsername();
			channelName = currentUser.compareTo(secondUser) < 0 ? currentUser + secondUser : secondUser + currentUser;
		} else {
			channelName = credentials.getName();
		}
		
		if (DataManager.getChannelByName(conn, channelName) == null) { // channel with this name does not exist yet
			Channel channel = DataManager.addChannel(conn, credentials); // create channel
			DataManager.addSubscription(conn, new Subscription(credentials.getName(), chatUsers.get(session).getUsername()), new Timestamp(System.currentTimeMillis())); // subscribe the creator to the channel
			if (credentials.getUsername() != null) { // private channel
				DataManager.addSubscription(conn, new Subscription(credentials.getName(), credentials.getUsername()), new Timestamp(System.currentTimeMillis()));
				User secondUser = DataManager.getUserByUsername(conn, credentials.getUsername());
				channel.setChannelName(channelName);
				DataManager.updateChannelUsers(conn, channel);
				System.out.println("created this channel: " + gson.toJson(new ChannelSuccess(channel)));
				doNotify(session, gson.toJson(new ChannelSuccess(channel)));
				doNotify(secondUser, gson.toJson(BuildSuccessMessages.buidSubscribeSuccess(conn, channel))); // it's a private channel (i.e. *not* public)
			} else { // public channel
				doNotify(session, gson.toJson(new ChannelSuccess(channel)));
			}
		} else { // channel exists
			doNotify(session, gson.toJson(new ChannelFailure(credentials.getName(), "Channel already exists")));
		}
	}

	private void handleSubscribeToChannelRequest(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
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
						doNotify(session, gson.toJson(subscribeSucces));
						// notify the all the users in this channel of the new subscription
						doNotifyByChannel(
								channel,
								gson.toJson(new UserSubscribed(
										channel.getChannelName(),
										ThreadUser.getThreadUserByUser(DataManager.getUserByUsername(conn, credentials.getUsername())))),
								session); // unsubscribe the user on all other clients
					} else {
						doNotify(session, gson.toJson(new SubscribeFailure("General error")));
					}
				} else {
					doNotify(session, gson.toJson(new SubscribeFailure("Already subscribed to channel")));
				}
			} else {
				doNotify(session, gson.toJson(new SubscribeFailure("Cannot subscribe to a private channel")));
			}
		} else { // channel doesn't exist
			doNotify(session, gson.toJson(new SubscribeFailure("Channel does not exist")));
		}
	}

	private void handleUnsubscribeToChannelRequest(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		Subscription credentials = gson.fromJson(msgContent, Subscription.class);
		Channel channel = null;
		credentials.setUsername(chatUsers.get(session).getUsername());
		if ((channel = DataManager.getChannelByName(conn, credentials.getChannelName())) != null) { // check if channel exists
			if (DataManager.getSubscriptionByChannelAndUsername(conn, credentials.getChannelName(), credentials.getUsername()) != null) { // if subscribed
				DataManager.removeSubscription(conn, credentials);
				channel.setNumberOfSubscribers(channel.getNumberOfSubscribers() - 1);
				DataManager.updateChannel(conn, channel);
				DataManager.updateChannelUsers(conn, channel);
				doNotify(session, gson.toJson(new Unsubscribe(credentials.getChannelName())));
				// update the remaining users in the channel that the user has quit
				doNotifyByChannel(
						channel,
						gson.toJson(new UserUnsubscribed(channel.getChannelName(), credentials.getUsername())),
						session); // unsubscribe the user on all other clients
			} else {
				doNotify(session, gson.toJson(new Unsubscribe("Not subscribed to channel")));
			}
		} else { // channel doesn't exist
			doNotify(session, gson.toJson(new Unsubscribe("Channel does not exist")));
		}
	}
	
	private void handleChannelDiscoveryRequest(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		ChannelDiscovery credentials = gson.fromJson(msgContent, ChannelDiscovery.class);
		System.out.println("discovery reply: " + gson.toJson(new Discovery(DataManager.discoverChannels(conn, credentials))));
		doNotify(session, gson.toJson(new Discovery(DataManager.discoverChannels(conn, credentials))));
	}
	
	private void handleChannelViewingRequest(Session session, Connection conn, JsonObject msgContent) {
		Gson gson = new Gson();
		ChannelViewing credentials = gson.fromJson(msgContent, ChannelViewing.class);
		chatViewedChannels.put(session, DataManager.getChannelByName(conn, credentials.getChannel()));
	}
	
	private void handleDownloadMessagesRequest(Session session, Connection conn, JsonObject msgContent) throws IOException {
		Gson gson = new Gson();
		MessageDownload credentials = gson.fromJson(msgContent, MessageDownload.class);
		Channel channel = DataManager.getChannelByName(conn, credentials.getChannel());
		if (channel != null) {
			Map<String, ThreadUser> usersMap = DataManager.getMapOfAllUsers(conn); // map of all users
			Subscription subscription = DataManager.getSubscriptionByChannelAndUsername(conn, channel.getChannelName(), chatUsers.get(session).getUsername()); // the subscription of this user to the required channel
			Map<String, Map<Integer, MessageThread>> channelDownloadedMessages = downloadedMessages.get(session); // get all the messages that were downloaded during this session
			if (channelDownloadedMessages == null) { // if first time viewing then:
				downloadedMessages.put(session, channelDownloadedMessages = new HashMap<>()); // create new history of messages
				channelDownloadedMessages = new HashMap<>();
			}
			Map<Integer, MessageThread> channelThread = channelDownloadedMessages.get(channel.getChannelName());
			if (channelThread == null) {
				channelDownloadedMessages.put(channel.getChannelName(), (channelThread = new HashMap<>()));
			}
			ArrayList<Message> messages = DataManager.getMessagesByChannelNameAndTimetamp(conn, usersMap, channel.getChannelName(), subscription.getSubscriptionTime()); // gets all the messages in the channel since subscription
			int lastRead = 0;
			if (!messages.isEmpty()) { // if channel thread is not empty
				Collection<MessageThread> requiredMessages = new ArrayList(); // the messages that will be sent
				for (Message message : messages) {
					if (lastRead >= AppConstants.MESSAGES_TO_DOWNLOAD) {
						break;
					}
					if (channelThread == null)
						System.out.println("channelThread is null");
					if (message == null)
						System.out.println("message is null");
					if (channelThread.get(message.getId()) == null) {
						MessageThread messageThread = new MessageThread(message);
						lastRead++;
						requiredMessages.add(messageThread);
						channelThread.put(message.getId(), messageThread);
					}
				}
				int maxId = subscription.getLastReadMessageId();
				for (MessageThread message : requiredMessages) { // iterate over the messages that will be sent
					if (message.getMessage().getId() > maxId) {
						maxId = message.getMessage().getId();
						subscription.setUnreadMessages(subscription.getUnreadMessages() - 1);
						if (message.getMessage().getContent().contains("@" + usersMap.get(message.getMessage().getUser().getNickname()))) {
							subscription.setUnreadMentionedMessages(subscription.getUnreadMentionedMessages() - 1);
						}
						subscription.setLastReadMessageId(maxId);
					}
				}
				DataManager.updateSubscription(conn, subscription);
				System.out.println("downloaded these messages: " + gson.toJson(new DownloadMessages(channel.getChannelName(), requiredMessages, subscription.getUnreadMessages(), subscription.getUnreadMentionedMessages())));
				doNotify(session, gson.toJson(new DownloadMessages(channel.getChannelName(), requiredMessages, subscription.getUnreadMessages(), subscription.getUnreadMentionedMessages())));
			} else { // no messages in channel
				doNotify(session, gson.toJson(new DownloadMessages(channel.getChannelName(), null, 0, 0)));
			}
		} else { // channel was not found
			doNotify(session, gson.toJson(new DownloadMessages(credentials.getChannel(), null, 0, 0)));
		}
	}

	private void handleReceivedMessageRequest(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		System.out.println("the message is: " + msgContent);
		Gson gson = new Gson();
		MessageCredentials credentials = gson.fromJson(msgContent, MessageCredentials.class);
		Channel channel = DataManager.getChannelByName(conn, credentials.getMessage().getChannelId()); // get the required channel
		if (channel != null) { // check if channel exists
			User user = chatUsers.get(session);
			if (DataManager.getSubscriptionByChannelAndUsername(conn, credentials.getMessage().getChannelId(), user.getUsername()) != null) { // check if subscribed
				DataManager.updateChannelUsers(conn, channel); // get the channel's users
				Message message = DataManager.addMessage(conn, credentials.getMessage(), new ThreadUser(user.getUsername(), user.getNickname(), user.getDescription(), user.getAvatarUrl())); // parse the message
				if (message.getRepliedToId() >= 0) {
					Message parentMessage = null;
					do {
						parentMessage = DataManager.getMessageByID(conn, message.getRepliedToId());
					} while (parentMessage.getRepliedToId() >= 0);
					DataManager.updateThread(conn, parentMessage, message.getLastModified());
				}
				doNotify(session, gson.toJson(new MessageReceived())); // update the sender that his message was received
				for (ThreadUser thUser : channel.getUsers()) {
					Subscription subscription = DataManager.getSubscriptionByChannelAndUsername(conn, channel.getChannelName(), thUser.getUsername());
					subscription.setUnreadMessages(subscription.getUnreadMessages() + 1); // mark as unread
					if (message.getContent().contains("@" + thUser.getNickname())) { // check if current user was mentioned
						subscription.setUnreadMentionedMessages(subscription.getUnreadMentionedMessages() + 1); // update that there is an unread mention
					}
					doNotify(thUser, gson.toJson(new IncomingMessage(channel.getChannelName(), message, subscription.getUnreadMessages(), subscription.getUnreadMentionedMessages()))); // update all users in chat about the new message
					DataManager.updateSubscription(conn, subscription);
				}
			} else {
				doNotify(session, gson.toJson(new MessageReceived("Not subscribed to channel")));
			}
		} else { // channel doesn't exist
			doNotify(session, gson.toJson(new MessageReceived("Channel does not exist")));
		}
	}
}
