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

public final class BuildSuccessMessages {
	
	private BuildSuccessMessages() {} // making this class none-initializable ("pure static")
	
	public static AuthSuccess buildAuthSuccess(Connection conn, UserCredentials credentials, ThreadUser user) {
		AuthSuccess authSuccess = new AuthSuccess(user);
		Map<String, ThreadUser> mapUsernameToNickname = DataManager.getMapOfAllUsers(conn);
		for (Channel channel : DataManager.getAllChannels(conn)) { // iterate over each channel
			Channel subscribedChannel = null, privateChannel = null;
			for (Subscription subscription : DataManager.getSubscriptionsByChannelName(conn, channel.getChannelName())) { // iterate over the subscriptions
				channel.addUser(mapUsernameToNickname.get(subscription.getUsername())); // add user to channel
				if (subscription.getUsername().equals(credentials.getUsername())) { // check if the logged user is subscribed to this channel
					Channel copy;
					if (channel.isPublic()) {
						copy = subscribedChannel = new Channel(channel.getChannelName(), channel.getDescription(), channel.getNumberOfSubscribers(), true);
					} else {
						copy = privateChannel = new Channel(channel.getChannelName(), channel.getDescription(), channel.getNumberOfSubscribers(), false);
					}

					for (ThreadUser thUser : channel.getUsers()) {
						copy.addUser(thUser);
					}
					
					copy.setUnreadMessages(subscription.getUnreadMessages());
					copy.setUnreadMentionedMessages(subscription.getUnreadMentionedMessages());
				}
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
