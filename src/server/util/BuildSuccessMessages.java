package server.util;

import java.sql.Connection;
import java.util.Map;

import server.DataManager;
import server.messages.AuthSuccess;
import server.messages.SubscribeSuccess;
import server.model.Channel;
import server.model.Subscription;
import server.model.ThreadUser;
import server.model.UserCredentials;

/**
 * A utility class to simplify building a AuthSuccess Message
 * @author Ilia and Michael
 *
 */
public final class BuildSuccessMessages {
	
	private BuildSuccessMessages() {} // making this class none-initializable ("pure static")
	
	/**
	 * Builds an AuthSuccess message (the message that is sent when a user logs in successfully)
	 * @param conn the connection to the database
	 * @param credentials the users credentials
	 * @param user the user that logged in
	 * @return returns the AuthSuccess message that will be sent to the client
	 */
	public static AuthSuccess buildAuthSuccess(Connection conn, UserCredentials credentials, ThreadUser user) {
		AuthSuccess authSuccess = new AuthSuccess(user);
		Map<String, ThreadUser> mapUsernameToNickname = DataManager.getMapOfAllUsers(conn);
		for (Channel channel : DataManager.getAllChannels(conn)) { // iterate over each channel
			Channel subscribedChannel = null, privateChannel = null;
			boolean isSubscribed = false;
			int unread = 0, mentioned = 0;
			for (Subscription subscription : DataManager.getSubscriptionsByChannelName(conn, channel.getChannelName())) { // iterate over the subscriptions
				channel.addUser(mapUsernameToNickname.get(subscription.getUsername())); // add user to channel
				if (subscription.getUsername().equals(credentials.getUsername())) { // check if the logged user is subscribed to this channel
					isSubscribed = true; // if that's the user, mark that the user is subscribed to this channel
					unread = subscription.getUnreadMessages();
					mentioned = subscription.getUnreadMentionedMessages();
				}
			}

			// if this user is subscribed to the channel
			if (isSubscribed) {
				Channel copy;
				if (channel.isPublic()) {
					copy = subscribedChannel = new Channel(channel.getChannelName(), channel.getDescription(), channel.getNumberOfSubscribers(), true);
				} else {
					copy = privateChannel = new Channel(channel.getChannelName(), channel.getDescription(), channel.getNumberOfSubscribers(), false);
				}

				for (ThreadUser thUser : channel.getUsers()) {
					copy.addUser(thUser);
				}
				
				copy.setUnreadMessages(unread);
				copy.setUnreadMentionedMessages(mentioned);
			}

			if (subscribedChannel != null) {
				authSuccess.addSubscribedChannel(subscribedChannel);
			}

			if (privateChannel != null) {
				authSuccess.addPrivateChannel(privateChannel);
			}
		}
		mapUsernameToNickname.clear();
		return authSuccess;
	}
	
	public static SubscribeSuccess buidSubscribeSuccess(Connection conn, Channel channel) {
		SubscribeSuccess subscribeSuccess = new SubscribeSuccess(channel);
		DataManager.updateChannelUsers(conn, channel); // update channel's users list
		return subscribeSuccess;
	}
}