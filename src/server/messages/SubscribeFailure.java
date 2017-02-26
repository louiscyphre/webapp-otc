package server.messages;

public class SubscribeFailure {
	
	private String messageType;
	private String error;
	
	public SubscribeFailure(String error) {
		this.messageType = "subscribeFailure";
		this.error = error;
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
