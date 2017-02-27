package server.messages;

/**
 * The message sent to the client when channel creation fails
 * @author Ilia and Michael
 *
 */
public class ChannelFailure {
	
	private String messageType = "channelFailure"; // the type of message 
	private String channelId; // the channel's name
	private String error; // the error that occured
	
	public ChannelFailure(String channel, String error) {
		this.channelId = channel;
		this.error = error;
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
		channelId = channel;
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
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthFailure [MessageType=" + messageType + ", Error=" + error + "]";
	}
}
