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
			prepStmt.setString(2, user.getPassword());
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
	 * Returns a channel by given channel name, including its subscribers
	 * @param conn
	 * @param channelName
	 * @return
	 */
	public static Channel updateChannelUsers(Connection conn, Channel channel) {
		Collection<Subscription> subscriptions = getSubscriptionsByChannelName(conn, channel.getChannelName());
		for (Subscription subscription : subscriptions) {
			if (subscriptions != null) {
				User user = getUserByUsername(conn, subscription.getUsername());
				if (user != null) {
					channel.getUsers().add(ThreadUser.getThreadUserByUser(user));
				}
			}
		}
		return channel;
	}
	
	/**
	 * Adds a new channel to the database
	 * @param conn the connection to the database
	 * @param credentials the channel's details
	 */
	public static Channel addChannel(Connection conn, ChannelCredentials credentials) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.INSERT_CHANNEL_STMT);
			prepStmt.setString (1, credentials.getName());
			prepStmt.setString (2, credentials.getDescription());
			prepStmt.setInt    (3, 0);
			prepStmt.setBoolean(4, credentials.getUsername() == null);
			prepStmt.execute();
			prepStmt.close();
			return new Channel(credentials.getName(), credentials.getDescription(), 0, credentials.getUsername() == null);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
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
			rs.close();
			prepStmt.close();
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
				subscription = new Subscription(channelName, userName, rs.getTimestamp(3), rs.getInt(4), rs.getInt(5), rs.getInt(6));
			}
			rs.close();
			prepStmt.close();
			return subscription;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void addSubscription(Connection conn, Subscription subscription, Timestamp subscriptionTime) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.INSERT_SUBSCRIPTION_STMT);
			prepStmt.setString(1, subscription.getChannelName());
			prepStmt.setString(2, subscription.getUsername());
			prepStmt.setTimestamp(3, subscriptionTime);
			prepStmt.setInt(4, subscription.getLastReadMessageId());
			prepStmt.setInt(5, subscription.getUnreadMessages());
			prepStmt.setInt(6, subscription.getUnreadMentionedMessages());
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
    
    public static void updateSubscription(Connection conn, Subscription subscription) {
    	try {
    		PreparedStatement prepStmt = conn.prepareStatement(AppConstants.UPDATE_SUBSCRIPTION_STMT);
    		prepStmt.setInt(1, subscription.getLastReadMessageId());
    		prepStmt.setInt(2, subscription.getUnreadMessages());
    		prepStmt.setInt(3, subscription.getUnreadMentionedMessages());
    		prepStmt.setString(4, subscription.getChannelName());
    		prepStmt.setString(5, subscription.getUsername());
    		prepStmt.execute();
    		prepStmt.close();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Returns a message by an ID number
     * @param conn the connection to the database
     * @param id the id to search for
     * @return the message with the given id. if not found: null.
     */
    public static Message getMessageByID(Connection conn, int id) {
    	try {
    		Message message = null;
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_MESSAGES_BY_CHANNEL_STMT);
			prepStmt.setInt(1, id);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				message = new Message(id, rs.getString(2), rs.getString(3), rs.getTimestamp(4), rs.getTimestamp(5), rs.getInt(6), rs.getString(7)); 
			}
			rs.close();
			prepStmt.close();
			return message;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    }

	/**
	 * Returns all the message in a certain channel from a date
	 * @param conn the connection to the database
	 * @param usersMap map of users' names to users
	 * @param channelName the channel's name
	 * @param startTime the time from which to return messages
	 * @return all messages in channel
	 */
	public static ArrayList<Message> getMessagesByChannelNameAndTimetamp(Connection conn, Map<String, ThreadUser> usersMap, String channelName, Timestamp startTime) {
		ArrayList<Message> messages = new ArrayList<>();
		try {
			System.out.println("startdate: " + startTime);
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_MESSAGES_BY_CHANNEL_STMT);
			prepStmt.setString(1, channelName);
			prepStmt.setTimestamp(2, startTime);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) { // iterate over all found messages
				messages.add(new Message(rs.getInt(1), rs.getString(2), usersMap.get(rs.getString(3)), rs.getTimestamp(4), rs.getTimestamp(5), rs.getInt(6), rs.getString(7)));
			}
			rs.close();
			prepStmt.close();
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
			prepStmt.setTimestamp(4, (messageTime));
			prepStmt.setInt(5, credentials.getReplyToID());
			prepStmt.setString(6, credentials.getContent());
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
	
	public static void updateThread(Connection conn, Message rootMessage, Timestamp newTime) {
		try {
			rootMessage.setLastModified(newTime);
			PreparedStatement stmt = conn.prepareStatement(AppConstants.UPDATE_MESSAGE_LASTMODIFIED_STMT);
			stmt.setTimestamp(1, newTime);
			stmt.setInt(2, rootMessage.getId());
			stmt.execute();
			stmt.close();
			
			PreparedStatement repliesStmt = conn.prepareStatement(AppConstants.SELECT_MESSAGE_BY_REPLY_TO_ID_STMT);
			repliesStmt.setInt(1, rootMessage.getId());
			ResultSet repliesRs = repliesStmt.executeQuery();
			
			while (repliesRs.next()) {
				updateThread(conn, new Message(
						repliesRs.getInt(1),
						repliesRs.getString(2),
						repliesRs.getString(3),
						repliesRs.getTimestamp(4),
						repliesRs.getTimestamp(5),
						repliesRs.getInt(6),
						repliesRs.getString(7)), newTime);
			}
			
			repliesRs.close();
			repliesStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Collection<Channel> discoverChannels(Connection conn, ChannelDiscovery query) {
		try {
			Collection<Channel> channels = new ArrayList<>(); // the list that will be returned
			
			PreparedStatement chanStmt = conn.prepareStatement(AppConstants.SELECT_CHANNEL_BY_CHANNELNAME_LIKENESS_STMT);
			chanStmt.setString(1, "%" + query.getQuery() + "%");				
			ResultSet chanRS = chanStmt.executeQuery();
			if (chanRS.next()) {
				Channel channel = new Channel(chanRS.getString(1), chanRS.getString(2), chanRS.getInt(3), chanRS.getBoolean(4));
				if (!channels.contains(channel)) {
					updateChannelUsers(conn, channel);
					channels.add(channel);
				}
			}
			chanRS.close();
			chanStmt.close();
			
			PreparedStatement nickStmt = conn.prepareStatement(AppConstants.SELECT_USER_BY_NICKNAME_LIKENESS_STMT);
			nickStmt.setString(1, "%" + query.getQuery() + "%");
			ResultSet nickRS = nickStmt.executeQuery();
			while (nickRS.next()) { // iterate over all the users who's nickname is this
				String username = nickRS.getString(1); // the username of the user
				PreparedStatement subByUserStmt = conn.prepareStatement(AppConstants.SELECT_SUBSCRIPTIONS_BY_USERNAME_STMT);
				subByUserStmt.setString(1, username);
				ResultSet subByUserRS = subByUserStmt.executeQuery();
				while (subByUserRS.next()) {
					PreparedStatement chan2Stmt = conn.prepareStatement(AppConstants.SELECT_PUBLIC_CHANNEL_BY_CHANNELNAME_STMT);
					chan2Stmt.setString(1, subByUserRS.getString(1));
					ResultSet chan2RS = chan2Stmt.executeQuery();
					if (chan2RS.next()) {
						Channel channel = new Channel(chan2RS.getString(1), chan2RS.getString(2), chan2RS.getInt(3), chan2RS.getBoolean(4));
						if (!channels.contains(channel)) {
							updateChannelUsers(conn, channel); 
							channels.add(channel);
						}
					}
					chan2RS.close();
					chan2Stmt.close();
				}
				subByUserRS.close();
				subByUserStmt.close();
			}
			nickRS.close();
			nickStmt.close();
			
			return channels;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
