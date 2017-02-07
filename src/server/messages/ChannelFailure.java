package server.messages;

public class ChannelFailure {
	
	private String MessageType;
	private String Error;
	
	public ChannelFailure(String error) {
		this.MessageType = "ChannelFailure";
		this.Error = error;
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
		return "AuthFailure [MessageType=" + MessageType + ", Error=" + Error + "]";
	}
}
