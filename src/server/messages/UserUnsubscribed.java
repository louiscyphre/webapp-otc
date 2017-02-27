package server.messages;

/**
 * This message is sent to all users in a channel informing them that a user has quit the channel
 * @author Ilia and Michael
 *
 */
public class UserUnsubscribed {
	
	private String messageType = "userUnsubscribed"; // the type of message
	private String channelId; // the name of the channel
	private String username; // the user's username
	
	public UserUnsubscribed(String channel, String username) {
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
