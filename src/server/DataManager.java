/*
 *     webapp-otc - an online collaboration tool .
 *     Copyright (C) 2017 Ilia Butvinnik and Michael Goldman
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import java.util.Map.Entry;

import server.model.Channel;
import server.model.ChannelCredentials;
import server.model.ChannelDiscovery;
import server.model.Message;
import server.model.MessageThread;
import server.model.Subscription;
import server.model.ThreadUser;
import server.model.User;

/**
 * Static class that manages the communication between server and the database
 * @author Ilia and Michael
 * 
 */
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
	 * Returns a channel by given channel, including its subscribers
	 * @param conn the connection to the database
	 * @param channel the required channel
	 * @return returns the channel with updated users' list
	 */
	public static Channel updateChannelUsers(Connection conn, Channel channel) {
		channel.getUsers().clear();
		Collection<Subscription> subscriptions = getSubscriptionsByChannelName(conn, channel.getChannelName());
		for (Subscription subscription : subscriptions) {
			if (subscriptions != null) {
				User user = getUserByUsername(conn, subscription.getUsername());
				if (user != null) {
					channel.getUsers().add(ThreadUser.getThreadUserByUser(user));
				}
			}
		}
		channel.setNumberOfSubscribers(channel.getUsers().size());
		return channel;
	}
	
	/**
	 * Adds a new channel to the database
	 * @param conn the connection to the database
	 * @param credentials the channel's details
	 * @return returns the channel matching the given credentials
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
				subscriptions.add(new Subscription(channelName, rs.getString(2), rs.getTimestamp(3), rs.getInt(4), rs.getInt(5), rs.getInt(6)));
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
	
	/**
	 * Adds a subscription of a user to a channel into the database
	 * @param conn the connection to the database
	 * @param subscription the subscription to add
	 * @param subscriptionTime the time when the subscription was made (server time)
	 */
	public static void addSubscription(Connection conn, Subscription subscription, Timestamp subscriptionTime) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.INSERT_SUBSCRIPTION_STMT);
			prepStmt.setString(1, subscription.getChannelName());
			prepStmt.setString(2, subscription.getUsername());
			prepStmt.setTimestamp(3, subscriptionTime);
			prepStmt.setInt(4, subscription.getNumberOfReadMessages());
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
	
	/**
	 * Removes a subscription of a user to a channel from the database
	 * @param conn the connection to the database
	 * @param subscription the subscription to remove
	 */
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
	 * Updates details of an existing subscription in the database
	 * @param conn the connection to the database
	 * @param subscription the subscription to update (including the new details)
	 */
    public static void updateSubscription(Connection conn, Subscription subscription) {
    	try {
    		PreparedStatement prepStmt = conn.prepareStatement(AppConstants.UPDATE_SUBSCRIPTION_STMT);
    		prepStmt.setInt(1, subscription.getNumberOfReadMessages());
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
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_MESSAGE_BY_ID_STMT);
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
     * Returns the nickname of the author of a given message
     * @param conn the connection to the database
     * @param id the id of the message which auther to look for
     * @return the nickname of the user who wrote the message
     */
    public static String getNicknameByMessageId(Connection conn, int id) {
    	try {
    		String nickname = "";
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_MESSAGE_BY_ID_STMT);
			prepStmt.setInt(1, id);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				nickname = getUserByUsername(conn, rs.getString(3)).getNickname();
			}
			rs.close();
			prepStmt.close();
			return nickname;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    }

    /**
     * Returns all the message in a certain channel from a certain date that are a reply to a message
     * @param conn the connection to the database
     * @param message the message which replies are to be looked for
     * @param usersMap the list of all users
     * @param channelName the name of the channel in which to search
     * @param startTime the date since a user's registration
     * @return returns all the messages of a channel since a given registration date that are a reply to a message
     */
    private static MessageThread getMessagesByChannelNameAndTimetamp(Connection conn, MessageThread message, Map<String, ThreadUser> usersMap, String channelName, Timestamp startTime) {
    	try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_MESSAGES_BY_CHANNEL_AND_REPLY_TO_ID_STMT);
			prepStmt.setString(1, channelName);
			prepStmt.setTimestamp(2, startTime);
			prepStmt.setInt(3, message.getMessage().getId());
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) { // iterate over all found messages
				message.addReply(new MessageThread(new Message(rs.getInt(1), rs.getString(2), usersMap.get(rs.getString(3)), rs.getTimestamp(4), rs.getTimestamp(5), rs.getInt(6), rs.getString(7))));
			}
			rs.close();
			prepStmt.close();
			
			for (MessageThread reply : message.getReplies()) {
				getMessagesByChannelNameAndTimetamp(conn, reply, usersMap, channelName, startTime);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return message;
    }
    
    /**
     * Removes and returns the last (nested) reply to the given message
     * @param message the message which reply to find
     * @return returns the required reply (or null if none exists)
     */
    private static MessageThread getLastReply(MessageThread message) {
    	if (message.getReplies().isEmpty()) { // if message doesn't have any replies, return null
    		return null;
    	} else {
    		MessageThread lastReply = message.getReplies().get(message.getReplies().size() - 1); // get last (not nested) reply 
    		if (!lastReply.getReplies().isEmpty()) { // if it has replies of its own, do recursion
    			return getLastReply(lastReply);
    		} else {
    			message.getReplies().remove(message.getReplies().size() - 1); // remove the last reply
    			return lastReply; // return it
    		}
    	}
    }
    
    /**
     * Returns the list of all messages in a channel sorted by last modified (since a user's subscription time)
     * @param conn the connection to the database
     * @param messages the list of all channel messages that will be returned
     * @param usersMap list of all users
     * @param channelName the name of the channel which messages to return
     * @param startTime the subscription time of the user
     * @return returns all the messages since subscription
     */
	public static ArrayList<MessageThread> getMessagesByChannelNameAndTimetamp(Connection conn, ArrayList<MessageThread> messages, Map<String, ThreadUser> usersMap, String channelName, Timestamp startTime) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.SELECT_MESSAGES_BY_CHANNEL_AND_REPLY_TO_ID_STMT);
			prepStmt.setString(1, channelName);
			prepStmt.setTimestamp(2, startTime);
			prepStmt.setInt(3, -1);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) { // iterate over all found messages
				messages.add(new MessageThread(new Message(rs.getInt(1), rs.getString(2), usersMap.get(rs.getString(3)), rs.getTimestamp(4), rs.getTimestamp(5), rs.getInt(6), rs.getString(7))));
			}
			rs.close();
			prepStmt.close();
			
			for (MessageThread message : messages) {
				getMessagesByChannelNameAndTimetamp(conn, message, usersMap, channelName, startTime);
			}
			
			for (int i = messages.size() - 1; i >= 0; i--) {
				MessageThread deepestReply = null;
				while ((deepestReply = getLastReply(messages.get(i))) != null) {
					messages.add(i + 1, deepestReply);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messages;
	}

	/**
	 * Adds a new messagae to the database
	 * @param conn the connection to the database
	 * @param message the new message that is to be added
	 * @param user the user who posted the message
	 * @return returns the updated message (including id number)
	 */
	public static Message addMessage(Connection conn, Message message, ThreadUser user) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement(AppConstants.INSERT_MESSAGE_STMT, new String[] { "ID" });
			message.setUser(user);
			message.setMessageTime(new Timestamp(System.currentTimeMillis()));
			message.setLastModified(message.getMessageTime());
			prepStmt.setString(1, message.getChannelId());
			prepStmt.setString(2, message.getUserId());
			prepStmt.setTimestamp(3, message.getMessageTime());
			prepStmt.setTimestamp(4, message.getLastModified());
			prepStmt.setInt(5, message.getRepliedToId());
			prepStmt.setString(6, message.getContent());
			prepStmt.execute();
			ResultSet rs = prepStmt.getGeneratedKeys();
			rs.next();
			message.setId(rs.getInt(1));
			rs.close();
			prepStmt.close();
			return message;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * updates the "Last Modified" timestamp of all messages in a message-thread (message and its replies)
	 * @param conn the connection to the database
	 * @param rootMessage the current parent of the thread (or subthread)
	 * @param newTime the new timestamp to give to the messages
	 * @param modifiedMessages used to return the IDs of all the messages of the thread
	 */
	public static void updateThread(Connection conn, Message rootMessage, Timestamp newTime, ArrayList<Integer> modifiedMessages) {
		try {
			rootMessage.setLastModified(newTime);
			PreparedStatement stmt = conn.prepareStatement(AppConstants.UPDATE_MESSAGE_LASTMODIFIED_STMT);
			stmt.setTimestamp(1, newTime);
			stmt.setInt(2, rootMessage.getId());
			stmt.execute();
			stmt.close();
			
			if (!modifiedMessages.contains(rootMessage.getId())) {
				modifiedMessages.add(rootMessage.getId());
			}
			
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
						repliesRs.getString(7)),
					newTime, modifiedMessages);
			}
			
			repliesRs.close();
			repliesStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Searches all public channel by a channel name and subscribed users' nicknames
	 * @param conn the connection to the database
	 * @param query the query that needs to be executed
	 * @return returns all the channels which name contains the given query or users' nicknames contain the query
	 */
	public static Collection<Channel> discoverChannels(Connection conn, ChannelDiscovery query) {
		try {
			Collection<Channel> channels = new ArrayList<>(); // the list that will be returned
			Collection<Channel> allChannels = new ArrayList<>(); // all the channels
			PreparedStatement chanStmt = conn.prepareStatement(AppConstants.SELECT_CHANNELS);
			ResultSet chanRS = chanStmt.executeQuery();
			while (chanRS.next()) {
				Channel channel = new Channel(chanRS.getString(1), chanRS.getString(2), chanRS.getInt(3), chanRS.getBoolean(4));
				updateChannelUsers(conn, channel);
				allChannels.add(channel);
				if (channel.isPublic() && channel.getChannelName().toLowerCase().contains(query.getQuery().toLowerCase()) && !channels.contains(channel)) {
					channels.add(channel);
				}
			}
			chanRS.close();
			chanStmt.close();
			
			for (Channel channel : allChannels) {
				if (channel.isPublic()) {
					for (ThreadUser user : channel.getUsers()) {
						if (user.getNickname().toLowerCase().contains(query.getQuery().toLowerCase()) && !channels.contains(channel)) {
							channels.add(channel);
						}
					}
				}
			}
			
			return channels;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean isInViewingWindow(Connection conn, Channel channel, Subscription subscription, Map<Integer, MessageThread> channelThread, Message message) {
		ArrayList<MessageThread> messages = new ArrayList<>();
		getMessagesByChannelNameAndTimetamp(conn, messages, getMapOfAllUsers(conn), channel.getChannelName(), subscription.getSubscriptionTime()); // gets all the messages in the channel since subscription ordered by 'last modified' and 'message date'
		int minDistance = Integer.MAX_VALUE, targetMessageIndex = -1;
		for (int i = 0; i < messages.size(); i++) {
			if (message.getId() == messages.get(i).getMessage().getId()) {
				targetMessageIndex = i;
			}
		}
		if (targetMessageIndex >= 0) {
			for (Entry<Integer, MessageThread> msgEntry : channelThread.entrySet()) {
				for (int i = 0; i < messages.size(); i++) {
					if (msgEntry.getKey() == messages.get(i).getMessage().getId()) {
						if (targetMessageIndex - i < minDistance) {
							minDistance = targetMessageIndex - i;
						}
					}
				}
			}
		}
		return ((minDistance > 0) && (minDistance <= AppConstants.MESSAGES_TO_DOWNLOAD));
	}
}
