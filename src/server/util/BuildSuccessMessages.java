package server.util;

import java.sql.Connection;
import java.util.Map;

import server.DataManager;
import server.messages.AuthSuccess;
import server.messages.SubscribeSuccess;
import server.model.Channel;
import server.model.Message;
import server.model.MessageThread;
import server.model.Subscription;
import server.model.ThreadUser;
import server.model.UserCredentials;

public final class BuildSuccessMessages {
	
	private BuildSuccessMessages() {} // making this class none-initializable ("pure static")
	
	public static AuthSuccess buildAuthSuccess(Connection conn, UserCredentials credentials, ThreadUser user) {
		AuthSuccess authSuccess = new AuthSuccess(user);
		Map<String, ThreadUser> mapUsernameToNickname = DataManager.getMapOfAllUsers(conn);
		for (Channel channel : DataManager.getAllChannels(conn)) { // iterate over each channel
			Channel publicChannel = null, subscribedChannel = null, privateChannel = null;
			boolean isSubscribed = false;
			for (Subscription subscription : DataManager.getSubscriptionsByChannelName(conn, channel.getChannelName())) { // iterate over the subscriptions
				channel.addUser(mapUsernameToNickname.get(subscription.getUsername())); // add user to channel
				if (subscription.getUsername().equals(credentials.getUsername())) { // check if the logged user is subscribed to this channel
					isSubscribed = true; // if that's the user, mark that the user is subscribed to this channel
				}
			}

			// if this user is subscribed to the channel, look for the thread's message history
			if (isSubscribed) {
				// iterate over all the messages in the thread
				for (Message message : DataManager.getMessagesByChannelName(conn, mapUsernameToNickname, channel.getChannelName())) {
					MessageThread messageThread = new MessageThread(message);
					// if current message is a reply to a previous message, "concatenate" them
					if (messageThread.getMessage().getRepliedToId() >= 0) {
						for (MessageThread addedMessage : channel.getChannelThread()) {
							if (addedMessage.getMessage().getId() == messageThread.getMessage().getRepliedToId()) {
								addedMessage.addReply(messageThread);
							}
						}
					} else { // current message is a message by itself (not a reply)
						channel.addMessage(messageThread);
					}
				}

				Channel copy;
				if (channel.isPublic()) {
					copy = subscribedChannel = new Channel(channel.getChannelName(), channel.getDescription(), channel.getNumberOfSubscribers(), true);
				} else {
					copy = privateChannel = new Channel(channel.getChannelName(), channel.getDescription(), channel.getNumberOfSubscribers(), false);
				}

				for (ThreadUser thUser : channel.getUsers()) {
					copy.addUser(thUser);
				}

				for (MessageThread thMessage : channel.getChannelThread()) {
					copy.addMessage(thMessage);
				}
			}

			if (channel.isPublic()) {
				publicChannel = new Channel(channel.getChannelName(), channel.getDescription(), channel.getNumberOfSubscribers(), true);
				for (ThreadUser thUser : channel.getUsers()) {
					publicChannel.addUser(thUser);
				}
			}

			if (publicChannel != null) {
				authSuccess.addPublicChannel(publicChannel);
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
		Map<String, ThreadUser> mapUsernameToNickname = DataManager.getMapOfAllUsers(conn);
		for (Subscription subscription : DataManager.getSubscriptionsByChannelName(conn, channel.getChannelName())) { // iterate over the subscriptions
			channel.addUser(mapUsernameToNickname.get(subscription.getUsername())); // add user to channel
		}

		// iterate over all the messages in the thread
		for (Message message : DataManager.getMessagesByChannelName(conn, mapUsernameToNickname, channel.getChannelName())) {
			MessageThread messageThread = new MessageThread(message);
			// if current message is a reply to a previous message, "concatenate" them
			if (messageThread.getMessage().getRepliedToId() >= 0) {
				for (MessageThread addedMessage : channel.getChannelThread()) {
					if (addedMessage.getMessage().getId() == messageThread.getMessage().getRepliedToId()) {
						addedMessage.addReply(messageThread);
					}
				}
			} else { // current message is a message by itself (not a reply)
				channel.addMessage(messageThread);
			}
		}
		mapUsernameToNickname.clear();
		return subscribeSuccess;
	}
}
