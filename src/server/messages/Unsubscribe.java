package server.messages;

public class Unsubscribe {
	
	private String MessageType;
	private String Channel;
	private String Error = null;
	
	public Unsubscribe(String channel) {
		this.MessageType = "Unsubscribe";
		this.Channel = channel;
	}
	
	public Unsubscribe(String channel, String error) {
		this.MessageType = "Unsubscribe";
		this.Channel = channel;
		this.Error = error;
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
	 * @return the error
	 */
	public String getError() {
		return Error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		Error = error;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubscribeFailure [MessageType=" + MessageType + ", Error=" + Error + "]";
	}
}
