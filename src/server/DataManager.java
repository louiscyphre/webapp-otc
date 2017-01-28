package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import server.model.Channel;
import server.model.ChannelCredentials;
import server.model.ChannelDiscovery;
import server.model.Message;
import server.model.MessageCredentials;
import server.model.Subscription;
import server.model.ThreadUser;
import server.model.User;

public final class DataManager {
	
	private DataManager() {} // making this class none-initializable ("pure static")

	/**
	 * Returns a User by user's name
	 * @param conn the connection to the database
	 * @param userName the user's name to be searched for
	 * @return the user with this name. if does not exist, returns null
	 */
	public static User getUserByUsername(Connection conn, String userName) {
		try {
			User user = null;
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_USER_BY_USERNAME_STMT);
			prepStmt.setString(1, userName);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) { // user was found
				user = new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)); 
			}
			rs.close();
			prepStmt.close();
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns a User by user's name and password
	 * @param conn the connection to the database
	 * @param userName the user's name to be searched for
	 * @param password the user's password to be verified
	 * @return the user with this name and password. if does not exist, returns null
	 */
	public static User getUserByCredentials(Connection conn, String userName, String password) {
		User user = null;
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_USER_BY_CREDENTIALS_STMT);
			prepStmt.setString(1, userName);
			prepStmt.setString(2, password);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) { // user was found
				user = new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)); 
			}
			rs.close();
			prepStmt.close();
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Saves a new user into the database
	 * @param conn the connection to the database
	 * @param user the user to be added
	 */
	public static void addUser(Connection conn, User user) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.INSERT_USER_STMT);
			prepStmt.setString(1, user.getUsername());
			prepStmt.setString(2, user.getPasswordHash());
			prepStmt.setString(3, user.getNickname());
			prepStmt.setString(4, user.getDescription());
			prepStmt.setString(5, user.getAvatarUrl());
			prepStmt.execute();
			prepStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Maps each user name to User
	 * @param conn the connection to the database	
	 * @return map of all users' names to Users
	 */
	public static Map<String, ThreadUser> getMapOfAllUsers(Connection conn) {
		Map<String, ThreadUser> usersMap = new HashMap<>();
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_USERS);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) { // iterate over all found users
				usersMap.put(rs.getString(1), new ThreadUser(rs.getString(1), rs.getString(3), rs.getString(4), rs.getString(5))); // add the user
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usersMap;
	}
	
	/**
	 * Returns all the channels in the database
	 * @param conn the connection to the database
	 * @return all the channels in the database
	 */
	public static Collection<Channel> getAllChannels(Connection conn) {
		Collection<Channel> channels = new ArrayList<>();
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_CHANNELS);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) { // iterate over all found channels
				channels.add(new Channel(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getBoolean(4))); // add the channel to the list
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return channels;
	}
	
	/**
	 * Returns a channel by given channel name
	 * @param conn the connection to the database
	 * @param channelName the required channel's name
	 * @return returns the corresponding channel
	 */
	public static Channel getChannelByName(Connection conn, String channelName) {
		try {
			Channel channel = null;
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_CHANNEL_BY_CHANNELNAME_STMT);
			prepStmt.setString(1, channelName);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) { // channel was found
				channel = new Channel(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getBoolean(4)); 
			}
			rs.close();
			prepStmt.close();
			return channel;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Adds a new channel to the database
	 * @param conn the connection to the database
	 * @param credentials the channel's details
	 */
	public static void addChannel(Connection conn, ChannelCredentials credentials) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.INSERT_CHANNEL_STMT);
			prepStmt.setString (1, credentials.getName());
			prepStmt.setString (2, credentials.getDescription());
			prepStmt.setInt    (3, 0);
			prepStmt.setBoolean(4, credentials.getUsername() == null);
			prepStmt.execute();
			prepStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the number of subscribers in the channel
	 * @param conn the connection to the database
	 * @param channel the channel to update
	 */
	public static void updateChannel(Connection conn, Channel channel) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.UPDATE_CHANNEL_SUBSCRIBERS_COUNT_STMT);
			prepStmt.setInt(1, channel.getNumberOfSubscribers());
			prepStmt.setString(2, channel.getChannelName());
			prepStmt.execute();
			prepStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns all the subscriptions by channel name
	 * @param conn the connection to the database
	 * @param channelName the channel's name to search through
	 * @return all the subscriptions to this channel
	 */
	public static Collection<Subscription> getSubscriptionsByChannelName(Connection conn, String channelName) {
		Collection<Subscription> subscriptions = new ArrayList<>();
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_SUBSCRIPTIONS_BY_CHANNEL_STMT);
			prepStmt.setString(1, channelName);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) { // iterate over all found subscriptions
				subscriptions.add(new Subscription(channelName, rs.getString(2)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subscriptions;
	}
	
	/**
	 * Returns a subscription by channel's name and user's name
	 * @param conn the connection to the database
	 * @param channelName the channel's name
	 * @param userName the user's name
	 * @return the subscription of the user to the channel. returns null if unsubscribed
	 */
	public static Subscription getSubscriptionByChannelAndUsername(Connection conn, String channelName, String userName) {
		try {
			Subscription subscription = null;
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_SUBSCRIPTIONS_BY_CHANNEL_AND_USER_STMT);
			prepStmt.setString(1, channelName);
			prepStmt.setString(2, userName);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) { // subscription was found
				subscription = new Subscription(channelName, userName);
			}
			rs.close();
			prepStmt.close();
			return subscription;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void addSubscription(Connection conn, Subscription subscription) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.INSERT_SUBSCRIPTION_STMT);
			prepStmt.setString(1, subscription.getChannelName());
			prepStmt.setString(2, subscription.getUsername());
			prepStmt.execute();
			prepStmt.close();
			Channel channel = DataManager.getChannelByName(conn, subscription.getChannelName());
			channel.setNumberOfSubscribers(channel.getNumberOfSubscribers() + 1);
			updateChannel(conn, channel);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeSubscription(Connection conn, Subscription subscription) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.REMOVE_SUBSCRIPTION_BY_CHANNEL_AND_USER_STMT);
			prepStmt.setString(1, subscription.getChannelName());
			prepStmt.setString(2, subscription.getUsername());
			prepStmt.execute();
			prepStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns all the message in a certain channel
	 * @param conn the connection to the database
	 * @param usersMap map of users' names to users
	 * @param channelName the channel's name
	 * @return all messages in channel
	 */
	public static Collection<Message> getMessagesByChannelName(Connection conn, Map<String, ThreadUser> usersMap, String channelName) {
		Collection<Message> messages = new ArrayList<>();
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_MESSAGES_BY_CHANNEL_STMT);
			prepStmt.setString(1, channelName);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) { // iterate over all found messages
				messages.add(new Message(rs.getInt(1), rs.getString(2), usersMap.get(rs.getString(3)), rs.getTimestamp(4), rs.getInt(5), rs.getString(6)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messages;
	}
	
	/**
	 * Adds a new message to the database
	 * @param conn the connection to the database
	 * @param credentials the details of the new message
	 */
	public static Message addMessage(Connection conn, MessageCredentials credentials, ThreadUser user) {
		try {
			Timestamp messageTime = null;
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.INSERT_MESSAGE_STMT, new String[] { "ID" });
			prepStmt.setString(1, credentials.getChannel());
			prepStmt.setString(2, credentials.getUsername());
			prepStmt.setTimestamp(3, (messageTime = new Timestamp(System.currentTimeMillis())));
			prepStmt.setInt(4, credentials.getReplyToID());
			prepStmt.setString(5, credentials.getContent());
			prepStmt.execute();
			ResultSet rs = prepStmt.getGeneratedKeys();
			rs.next();
			int id = rs.getInt(1);
			rs.close();
			prepStmt.close();
			return Message.getMessageByCredentials(id, credentials, user, messageTime);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Collection<Channel> discoverChannels(Connection conn, ChannelDiscovery query) {
		try {
			Collection<Channel> channels = new ArrayList<>();
			
			PreparedStatement chanStmt = conn.prepareStatement(AppConstants.SELECT_CHANNEL_BY_CHANNELNAME_STMT);
			chanStmt.setString(1, query.getQuery());				
			ResultSet chanRS = chanStmt.executeQuery();
			if (chanRS.next()) {
				channels.add(new Channel(chanRS.getString(1), chanRS.getString(2), chanRS.getInt(3), chanRS.getBoolean(4)));
			}
			chanRS.close();
			chanStmt.close();
			
			PreparedStatement subByUserStmt = conn.prepareStatement(AppConstants.SELECT_SUBSCRIPTIONS_BY_USERNAME_STMT);
			subByUserStmt.setString(1, query.getQuery());
			ResultSet subByUserRS = subByUserStmt.executeQuery();
			while (subByUserRS.next() && ChannelDiscovery.MAX_RESULTS > channels.size()) {
				PreparedStatement chan2Stmt = conn.prepareStatement(AppConstants.SELECT_CHANNEL_BY_CHANNELNAME_STMT);
				chan2Stmt.setString(1, subByUserRS.getString(1));
				ResultSet chan2RS = chan2Stmt.executeQuery();
				if (chan2RS.next()) {
					channels.add(new Channel(chan2RS.getString(1), chan2RS.getString(2), chan2RS.getInt(3), chan2RS.getBoolean(4)));
				}
				chan2RS.close();
				chan2Stmt.close();
			}
			subByUserRS.close();
			subByUserStmt.close();
			
			return channels;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}