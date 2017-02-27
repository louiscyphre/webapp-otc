package server.messages;

/**
 * This message is sent to a client whenver unsubscription of the channel is done successfully
 * @author Ilia and Michael
 *
 */
public class Unsubscribe {
	
	private String messageType = "unsubscribe"; // the type of message
	private String channelId; // the channel's name
	private String error = null; // the error (if no error then null)
	
	public Unsubscribe(String channel) {
		this.channelId = channel;
	}
	
	public Unsubscribe(String channel, String error) {
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
		this.channelId = channel;
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
		return "SubscribeFailure [MessageType=" + messageType + ", Error=" + error + "]";
	}
}
