package server.messages;

import server.model.ThreadUser;

public class UserSubscribed {
	
	private String MessageType;
	private String Channel;
	private ThreadUser User;
	
	public UserSubscribed(String channel, ThreadUser user) {
		this.MessageType = "UserSubscribed";
		this.Channel = channel;
		this.User = user;
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return MessageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		MessageType = messageType;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return Channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		Channel = channel;
	}

	/**
	 * @return the user
	 */
	public ThreadUser getUser() {
		return User;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(ThreadUser user) {
		User = user;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserSubscribed [MessageType=" + MessageType + ", Channel=" + Channel + ", User=" + User + "]";
	}
}
