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
import com.google.gson.JsonSyntaxException;

import server.AppConstants;
import server.DataManager;
import server.messages.ChannelFailure;
import server.messages.ChannelSuccess;
import server.messages.Discovery;
import server.messages.DownloadMessages;
import server.messages.MessageReceived;
import server.messages.SubscribeFailure;
import server.messages.SubscribeSuccess;
import server.messages.Unsubscribe;
import server.messages.UpdateCountersMessage;
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
 * Server-side WebSocket end-point that manages a chat over several channels between 
 * several client end-points
 * @author Ilia & Michael
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
						handleCreateChannelRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_SUBSCRIBE: // request to subscribe to a channel
						handleSubscribeToChannelRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_UNSUBSCRIBE: // request to unsubscribe from a channel
						handleUnsubscribeToChannelRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_CHANNEL_DISCOVERY: // request to search for channels
						handleChannelDiscoveryRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_CHANNEL_VIEWING: // notified that user is currently viewing this channel
						handleChannelViewingRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_DOWNLOAD_MESSAGES: // request to download 10 messages of a chat
						handleDownloadMessagesRequest(session, conn, msgContent);
						break;
					case AppConstants.MESSAGE_RECEIVED_MESSAGE: // request to deal with a received message
						handleReceivedMessageRequest(session, conn, msgContent);
						break;
					default:
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
	
	/**
	 * Returns a connection to the database using the tomcat context
	 * @return the connection to the database
	 */
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
	
	/**
	 * Checks if a user is currently viewing a channel discussion
	 * @param user the user to be checked
	 * @param channelName the name of the channel to be checked
	 * @return true if the user is viewing the channel, and false otherwise
	 */
	private boolean isViewingChannel(ThreadUser user, String channelName) {
		ArrayList<Channel> userChannels = new ArrayList<>();
		for (Entry<Session, User> userEntry : chatUsers.entrySet()) {
			Session session = userEntry.getKey();
			if (session.isOpen() && chatUsers.get(session).getUsername().equals(user.getUsername())) {
				Channel channel = chatViewedChannels.get(session) ;
				if (channel != null) {
					userChannels.add(channel);
				}
			}
		}
		
		for (Channel channel : userChannels) {
			if (channel.getChannelName().equals(channelName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Handler for a request to create a new channel (either public or private)
	 * @param session the session of the user that sent the request
	 * @param conn the connection to the database
	 * @param msgContent the content of the message sent by the user
	 * @throws IOException
	 * @throws SQLException
	 */
	private void handleCreateChannelRequest(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		ChannelCredentials credentials = gson.fromJson(msgContent, ChannelCredentials.class);
		String channelName;
		if (credentials.getUsername() != null) { // private channel
			String currentUser = chatUsers.get(session).getUsername(); // username of the sender (will be used to set the name of the channel in the database)
			String secondUser = credentials.getUsername(); // username of the second user (with whom the sender wants to create the channel) (will be used to set the name of the channel in the database)
			channelName = currentUser.compareTo(secondUser) < 0 ? currentUser + secondUser : secondUser + currentUser; // concatenate both usernames in alphabetical order
			credentials.setName(channelName);
		} else {
			channelName = credentials.getName();
		}
		
		if (DataManager.getChannelByName(conn, channelName) == null) { // channel with this name does not exist yet
			Channel channel = DataManager.addChannel(conn, credentials); // create channel
			DataManager.addSubscription(conn, new Subscription(credentials.getName(), chatUsers.get(session).getUsername()), new Timestamp(System.currentTimeMillis())); // subscribe the creator to the channel
			if (credentials.getUsername() != null) { // private channel
				DataManager.addSubscription(conn, new Subscription(credentials.getName(), credentials.getUsername()), new Timestamp(System.currentTimeMillis())); // subscribe the second user to the channel
				User secondUser = DataManager.getUserByUsername(conn, credentials.getUsername()); // get the details of the second user
				channel.setChannelName(channelName);
				DataManager.updateChannelUsers(conn, channel);
				doNotify(session, gson.toJson(new ChannelSuccess(channel))); // send reply to the creator of the channel informing him of success
				doNotify(secondUser, gson.toJson(BuildSuccessMessages.buidSubscribeSuccess(conn, channel))); // send message to the second user that he was added to a private channel
			} else { // public channel
				DataManager.updateChannelUsers(conn, channel);
				doNotify(session, gson.toJson(new ChannelSuccess(channel))); // send reply to the creator of the channel informing him of success
			}
		} else { // channel exists
			doNotify(session, gson.toJson(new ChannelFailure(credentials.getName(), "Channel already exists"))); // inform the creator of the channel that an error occured: a channel with this name already exists
		}
	}

	/**
	 * Handler for a request to subscribe to an existing channel
	 * @param session the session of the user that sent the request
	 * @param conn the connection to the database
	 * @param msgContent the content of the message sent by the user
	 * @throws IOException
	 * @throws SQLException
	 */
	private void handleSubscribeToChannelRequest(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		Subscription credentials = gson.fromJson(msgContent, Subscription.class);
		Channel channel = null;
		credentials.setUsername(chatUsers.get(session).getUsername());
		if ((channel = DataManager.getChannelByName(conn, credentials.getChannelName())) != null) { // check if channel exists
			if (channel.isPublic()) { // check if channel is public
				if (DataManager.getSubscriptionByChannelAndUsername(conn, channel.getChannelName(), credentials.getUsername()) == null) { // check if the user is not subscribed to the channel
					DataManager.addSubscription(conn, credentials, new Timestamp(System.currentTimeMillis()));
					channel.setNumberOfSubscribers(channel.getNumberOfSubscribers() + 1);
					DataManager.updateChannel(conn, channel);
					SubscribeSuccess subscribeSucces = BuildSuccessMessages.buidSubscribeSuccess(conn, channel);
					if (subscribeSucces != null) {
						doNotify(session, gson.toJson(subscribeSucces)); // notify the user that he was subscribed successfully to the channel
						doNotifyByChannel(
								channel,
								gson.toJson(new UserSubscribed(
										channel.getChannelName(),
										ThreadUser.getThreadUserByUser(DataManager.getUserByUsername(conn, credentials.getUsername())))),
								session); // notify all users, except the newly subscribed user, that a new user has been subscribed to the cahnnel
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

	/**
	 * Handler for a request to unsubscribe from a channel
	 * @param session the session of the user that sent the request
	 * @param conn the connection to the database
	 * @param msgContent the content of the message sent by the user
	 * @throws IOException
	 * @throws SQLException
	 */
	private void handleUnsubscribeToChannelRequest(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		Subscription credentials = gson.fromJson(msgContent, Subscription.class);
		Channel channel = null;
		credentials.setUsername(chatUsers.get(session).getUsername());
		if ((channel = DataManager.getChannelByName(conn, credentials.getChannelName())) != null) { // check if channel exists
			if (DataManager.getSubscriptionByChannelAndUsername(conn, credentials.getChannelName(), credentials.getUsername()) != null) { // check if user is subscribed
				DataManager.removeSubscription(conn, credentials);
				channel.setNumberOfSubscribers(channel.getNumberOfSubscribers() - 1);
				DataManager.updateChannel(conn, channel);
				DataManager.updateChannelUsers(conn, channel);
				doNotify(session, gson.toJson(new Unsubscribe(credentials.getChannelName()))); // notify the user that he was successfully unsubscribed from the desired channel
				doNotifyByChannel(
						channel,
						gson.toJson(new UserUnsubscribed(channel.getChannelName(), credentials.getUsername())),
						session); // notify all the users in the channel that a user has quit the channel
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
		doNotify(session, gson.toJson(new Discovery(DataManager.discoverChannels(conn, credentials))));
	}
	
	/**
	 * Updates the server what channel the user in this session is currently viewing
	 * @param session the current session
	 * @param conn the connection to the database
	 * @param msgContent the content of the message sent by the user
	 */
	private void handleChannelViewingRequest(Session session, Connection conn, JsonObject msgContent) {
		try {
			Gson gson = new Gson();
			ChannelViewing credentials = gson.fromJson(msgContent, ChannelViewing.class);
			chatViewedChannels.put(session, DataManager.getChannelByName(conn, credentials.getChannel()));
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends the next messages to the client (limited by AppConstants.MESSAGES_TO_DOWNLOAD (currently set to 10))
	 * @param session the current session
	 * @param conn the connection to the database
	 * @param msgContent the content of the message sent by the user
	 * @throws IOException
	 */
	private void handleDownloadMessagesRequest(Session session, Connection conn, JsonObject msgContent) throws IOException {
		Gson gson = new Gson();
		MessageDownload credentials = gson.fromJson(msgContent, MessageDownload.class);
		Channel channel = DataManager.getChannelByName(conn, credentials.getChannel());
		
		if (channel != null) { // check if channel exists
			downloadMessages(session, conn, channel);
		} else { // channel was not found
			doNotify(session, gson.toJson(new DownloadMessages(credentials.getChannel(), null, 0, 0)));
		}
	}

	/**
	 * Gets and sends the next messages to the client
	 * @param session the current session
	 * @param conn the connection to the database
	 * @param channel the channel which message are to be sent
	 * @throws IOException
	 */
	private void downloadMessages(Session session, Connection conn, Channel channel) throws IOException {
		Gson gson = new Gson();
		Map<String, ThreadUser> usersMap = DataManager.getMapOfAllUsers(conn); // map of all users
		Subscription subscription = DataManager.getSubscriptionByChannelAndUsername(conn, channel.getChannelName(), chatUsers.get(session).getUsername()); // the subscription of this user to the required channel
		Map<String, Map<Integer, MessageThread>> channelDownloadedMessages = downloadedMessages.get(session); // get all the messages that were downloaded during this session
		
		if (channelDownloadedMessages == null) { // if first time viewing then:
			downloadedMessages.put(session, (channelDownloadedMessages = new HashMap<>())); // create new history of messages
		}
		
		Map<Integer, MessageThread> channelThread = channelDownloadedMessages.get(channel.getChannelName()); // get the history of this user
		if (channelThread == null) { // if first time viewing this channel
			channelDownloadedMessages.put(channel.getChannelName(), (channelThread = new HashMap<>())); // create new history for this channnel
		}
		
		ArrayList<MessageThread> messages = new ArrayList<>();
		DataManager.getMessagesByChannelNameAndTimetamp(conn, messages, usersMap, channel.getChannelName(), subscription.getSubscriptionTime()); // gets all the messages in the channel since subscription ordered by 'last modified' and 'message date'
		int messagesRead = 0;
		if (!messages.isEmpty()) { // if channel thread is not empty
			Collection<MessageThread> requiredMessages = new ArrayList<>(); // the messages that will be sent
			for (int i = 0; i < messages.size() && messagesRead < AppConstants.MESSAGES_TO_DOWNLOAD; i++) { // read available messages, not more than maximal limit (10)
				Message message = messages.get(i).getMessage();
				if (channelThread.get(message.getId()) == null) {
					MessageThread messageThread = new MessageThread(message);
					messagesRead++;
					requiredMessages.add(messageThread);
					channelThread.put(message.getId(), messageThread);
				}
			}
			int maxId = subscription.getLastReadMessageId();
			for (MessageThread message : requiredMessages) { // iterate over the messages that will be sent
				if (message.getMessage().getId() > maxId) {
					maxId = message.getMessage().getId();
					subscription.setUnreadMessages(subscription.getUnreadMessages() - 1);
					if (message.getMessage().getContent().contains("@" + usersMap.get(chatUsers.get(session).getUsername()).getNickname())) {
						subscription.setUnreadMentionedMessages(subscription.getUnreadMentionedMessages() - 1);
					}
					subscription.setLastReadMessageId(maxId);
				}
			}
			DataManager.updateSubscription(conn, subscription);
			doNotify(session, gson.toJson(new DownloadMessages(channel.getChannelName(), requiredMessages, subscription.getUnreadMessages(), subscription.getUnreadMentionedMessages())));
		} else { // no messages in channel
			doNotify(session, gson.toJson(new DownloadMessages(channel.getChannelName(), null, 0, 0)));
		}
	}

	private void handleReceivedMessageRequest(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		MessageCredentials credentials = gson.fromJson(msgContent, MessageCredentials.class);
		Channel channel = DataManager.getChannelByName(conn, credentials.getMessage().getChannelId()); // get the required channel
		
		if (channel != null) { // check if channel exists
			if (credentials.getMessage().getRepliedToId() >= 0) {
				String originalUserNickname = DataManager.getNicknameByMessageId(conn, credentials.getMessage().getRepliedToId());
				credentials.getMessage().setContent("@" + originalUserNickname + " " + credentials.getMessage().getContent());
			}
			
			User user = chatUsers.get(session);
			if (DataManager.getSubscriptionByChannelAndUsername(conn, credentials.getMessage().getChannelId(), user.getUsername()) != null) { // check if subscribed
				DataManager.updateChannelUsers(conn, channel); // get the channel's users
				Message message = DataManager.addMessage(conn, credentials.getMessage(), new ThreadUser(user.getUsername(), user.getNickname(), user.getDescription(), user.getAvatarUrl())); // parse the message
				ArrayList<Integer> modifiedMessagesIDs = new ArrayList<>(); // if the new message is a reply, then will store the ID of all the messages in that thread
				if (message.getRepliedToId() >= 0) { // if a reply to a message
					Message parentMessage = message;
					do { // find the root of this discussion (the ancestor that is not a reply to any message)
						parentMessage = DataManager.getMessageByID(conn, parentMessage.getRepliedToId());
					} while (parentMessage.getRepliedToId() >= 0);
					DataManager.updateThread(conn, parentMessage, message.getLastModified(), modifiedMessagesIDs); // update the last modified time of the thread
				}
				
				for (ThreadUser thUser : channel.getUsers()) { // iterate over all the users in the channel
					Session userSession = null;
					for (Entry<Session, User> userEntry : chatUsers.entrySet()) { // iterate over all the open sessions and remove the replies from it
						if (userEntry.getKey().isOpen() && thUser.getUsername().equals(userEntry.getValue().getUsername())) { // if user is subscribed to the channel and has an open session
							userSession = userEntry.getKey();
							Map<String, Map<Integer, MessageThread>> channelDownloadedMessages = downloadedMessages.get(userSession); // get all the messages that were downloaded during this session
							if (channelDownloadedMessages != null) { // if viewing the channel then:
								Map<Integer, MessageThread> channelThread = channelDownloadedMessages.get(channel.getChannelName()); // get the history of this user
								if (channelThread != null) { // if downloaded messages already
									for (int messageToRemove : modifiedMessagesIDs) {
										channelThread.remove(messageToRemove);
									}
								}
							}
						}
					}

					Subscription subscription = DataManager.getSubscriptionByChannelAndUsername(conn, channel.getChannelName(), thUser.getUsername());
					subscription.setUnreadMessages(subscription.getUnreadMessages() + 1); // mark as unread
					if (message.getContent().contains("@" + thUser.getNickname())) { // check if current user was mentioned
						subscription.setUnreadMentionedMessages(subscription.getUnreadMentionedMessages() + 1); // update that there is an unread mention
					}
					DataManager.updateSubscription(conn, subscription);
					
					if (isViewingChannel(thUser, channel.getChannelName())) { // if user is viewing this channel
						Map<String, Map<Integer, MessageThread>> channelDownloadedMessages = downloadedMessages.get(userSession); // get all the messages that were downloaded during this session
						if (channelDownloadedMessages == null) { // if first time viewing then:
							downloadedMessages.put(userSession, (channelDownloadedMessages = new HashMap<>())); // create new history of messages
						}
						Map<Integer, MessageThread> channelThread = channelDownloadedMessages.get(channel.getChannelName()); // get the history of this user
						if (channelThread == null) { // if first time viewing this channel
							channelDownloadedMessages.put(channel.getChannelName(), (channelThread = new HashMap<>())); // create new history for this channnel
						}
						
						if (channelThread.size() < AppConstants.MESSAGES_TO_DOWNLOAD || DataManager.isInViewingWindow(conn, channel, subscription, channelThread, message)) { // if barely any messages downloaded, send next batch
							downloadMessages(userSession, conn, channel);
						} else {
							doNotify(thUser, gson.toJson(new UpdateCountersMessage(channel.getChannelName(), subscription.getUnreadMessages(), subscription.getUnreadMentionedMessages()))); // update all users in chat about the new message
						}
					} else { // user is subscribed, but not viewing the channel at the moment
						doNotify(thUser, gson.toJson(new UpdateCountersMessage(channel.getChannelName(), subscription.getUnreadMessages(), subscription.getUnreadMentionedMessages()))); // update all users in chat about the new message
					}
				}
			} else {
				doNotify(session, gson.toJson(new MessageReceived("Not subscribed to channel")));
			}
		} else { // channel doesn't exist
			doNotify(session, gson.toJson(new MessageReceived("Channel does not exist")));
		}
	}
}
