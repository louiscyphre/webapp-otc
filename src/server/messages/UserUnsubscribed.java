package server.messages;

public class UserUnsubscribed {
	
	private String MessageType;
	private String Channel;
	private String Username;
	
	public UserUnsubscribed(String channel, String username) {
		this.MessageType = "UserUnsubscribed";
		this.Channel = channel;
		this.Username = username;
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
	 * @return the username
	 */
	public String getUsername() {
		return Username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		Username = username;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserUnsubscribed [MessageType=" + MessageType + ", Channel=" + Channel + ", Username=" + Username + "]";
	}
}
