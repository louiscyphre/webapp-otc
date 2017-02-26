package server.messages;

public class UserUnsubscribed {
	
	private String messageType;
	private String channelId;
	private String username;
	
	public UserUnsubscribed(String channel, String username) {
		this.messageType = "userUnsubscribed";
		this.channelId = channel;
		this.username = username;
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserUnsubscribed [MessageType=" + messageType + ", Channel=" + channelId + ", Username=" + username + "]";
	}
}
