package server.websockets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
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
import server.model.Channel;
import server.model.ChannelCredentials;
import server.model.ChannelDiscovery;
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
					System.out.println("the following user logged in: " + username);
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
					case AppConstants.MESSAGE_RECEIVED_MESSAGE: // request to deal with a received message
						System.out.println("received \"Message\" request");
						handleReceivedMessage(session, conn, msgContent);
						break;
					}
				}
			}
		} catch (IOException | SQLException e) {
			session.close();
		}
	}

	/**
	 * Removes a client from the chat
	 * @param session
	 * 			client end point session
	 * @throws IOException
	 */
	@OnClose
	public void leaveChat(Session session) throws IOException{
		try {
			User user = chatUsers.remove(session);//fake user just for removal
			//let other participants know that client has left the chat
			doNotifyAll(null,"User <span class='username'>"+user+"</span> has left the chat...",session);
		} catch (IOException e) {
			session.close();
		} 
	}

	private void doNotifyAll(String author, String message, Session skip) throws IOException{
		for (Entry<Session,User> user : chatUsers.entrySet()){
			Session session = user.getKey();
			if (!session.equals(skip) && session.isOpen()){
				session.getBasicRemote().sendText((author != null ? "&gt&gt <span class='username'>"+author+"</span>: " : "")+ message+ " ("+new Date()+")");
			}
		}
	}

	private void doNotifyAll(User user, String message) throws IOException {
		for (Entry<Session,User> userEntry : chatUsers.entrySet()) {
			Session session = userEntry.getKey();
			if (session.isOpen()) {
				session.getBasicRemote().sendText(message);
			}
		}
	}

	private void doNotify(User user, String message) throws IOException {
		for (Entry<Session,User> userEntry : chatUsers.entrySet()) {
			Session session = userEntry.getKey();
			if (userEntry.getValue().getUsername().equals(user.getUsername()) && session.isOpen()) {
				session.getBasicRemote().sendText(message);
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
			DataManager.addChannel(conn, credentials); // create channel
			DataManager.addSubscription(conn, new Subscription(credentials.getName(), chatUsers.get(session).getUsername())); // subscribe the creator to the channel
			if (credentials.getUsername() != null) { // private channel
				DataManager.addSubscription(conn, new Subscription(credentials.getName(), credentials.getUsername()));
				User secondUser = DataManager.getUserByUsername(conn, credentials.getUsername());
				session.getBasicRemote().sendText(gson.toJson(new ChannelSuccess(secondUser.getNickname())));
				doNotify(secondUser, gson.toJson(BuildSuccessMessages.buidSubscribeSuccess(conn, new Channel(
						chatUsers.get(session).getUsername(), // the name of the channel is the creator's name
						credentials.getDescription(), // the description of the channel
						2, // the creator of the channel and this user => 2
						false)))); // it's a private channel (i.e. *not* public)
			} else { // public channel
				session.getBasicRemote().sendText(gson.toJson(new ChannelSuccess(credentials.getName())));
			}
		} else { // channel exists
			session.getBasicRemote().sendText(gson.toJson(new ChannelFailure("Channel already exists")));
		}
		conn.close();
	}

	private void handleSubscribeToChannelMessage(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		Subscription credentials = gson.fromJson(msgContent, Subscription.class);
		Channel channel = null;
		credentials.setUsername(chatUsers.get(session).getUsername());
		if ((channel = DataManager.getChannelByName(conn, credentials.getChannelName())) != null) { // check if channel exists
			if (channel.isPublic()) {
				if (DataManager.getSubscriptionByChannelAndUsername(conn, credentials.getChannelName(), credentials.getUsername()) == null) { // if unsubscribed
					DataManager.addSubscription(conn, credentials);
					channel.setNumberOfSubscribers(channel.getNumberOfSubscribers() + 1);
					DataManager.updateChannel(conn, channel);
					SubscribeSuccess subscribeSucces = BuildSuccessMessages.buidSubscribeSuccess(conn, channel);
					if (subscribeSucces != null) {
						session.getBasicRemote().sendText(gson.toJson(subscribeSucces));
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
		conn.close();
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
					session.getBasicRemote().sendText(gson.toJson(new Unsubscribe()));
				} else {
					session.getBasicRemote().sendText(gson.toJson(new Unsubscribe("Not subscribed to channel")));
				}
			} else {
				session.getBasicRemote().sendText(gson.toJson(new Unsubscribe("Cannot subscribe to a private channel")));
			}
		} else { // channel doesn't exist
			session.getBasicRemote().sendText(gson.toJson(new Unsubscribe("Channel does not exist")));
		}
		conn.close();
	}
	
	private void handleChannelDiscoveryMessage(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		ChannelDiscovery credentials = gson.fromJson(msgContent, ChannelDiscovery.class);
		session.getBasicRemote().sendText(gson.toJson(new Discovery(DataManager.discoverChannels(conn, credentials))));
		conn.close();
	}
	
	private void handleReceivedMessage(Session session, Connection conn, JsonObject msgContent) throws IOException, SQLException {
		Gson gson = new Gson();
		MessageCredentials credentials = gson.fromJson(msgContent, MessageCredentials.class);
		
		if (DataManager.getChannelByName(conn, credentials.getChannel()) != null) { // check if channel exists
			User user = null;
			if ((user = DataManager.getUserByUsername(conn, credentials.getUsername())) != null) { // check if user exists
				if (DataManager.getSubscriptionByChannelAndUsername(conn, credentials.getChannel(), credentials.getUsername()) != null) { // check if subscribed
					Message message = DataManager.addMessage(conn, credentials, new ThreadUser(user.getUsername(), user.getNickname(), user.getDescription(), user.getAvatarUrl()));
					session.getBasicRemote().sendText(gson.toJson(new MessageReceived()));
					doNotifyAll(user, gson.toJson(new IncomingMessage(message)));
				} else {
					session.getBasicRemote().sendText(gson.toJson(new MessageReceived("Not subscribed to channel")));
				}
			} else {
				session.getBasicRemote().sendText(gson.toJson(new MessageReceived("User does not exist")));
			}
		} else { // channel doesn't exist
			session.getBasicRemote().sendText(gson.toJson(new MessageReceived("Channel does not exist")));
		}
		conn.close();
	}
}
