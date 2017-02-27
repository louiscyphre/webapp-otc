package server.messages;

import server.model.ThreadUser;

/**
 * This message is sent to all users in a channel to notify them of an addition of a new user to the channel
 * @author Ilia and Michael
 *
 */
public class UserSubscribed {
	
	private String messageType = "userSubscribed"; // the type of message
	private String channelId; // the channel's name
	private ThreadUser user; // user's details (username, nickname, description and avatar)
	
	public UserSubscribed(String channel, ThreadUser user) {
		this.channelId = channel;
		this.user = user;
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channelId;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channelId = channel;
	}

	/**
	 * @return the user
	 */
	public ThreadUser getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(ThreadUser user) {
		this.user = user;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserSubscribed [MessageType=" + messageType + ", Channel=" + channelId + ", User=" + user + "]";
	}
}
