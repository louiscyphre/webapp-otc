package server.messages;

public class MessageReceived {
	
	private String messageType;
	private String error = null;
	
	public MessageReceived() {
		this.messageType = "messageReceived";
	}
	
	public MessageReceived(String error) {
		this.messageType = "messageReceived";
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
		return "MessageReceived [MessageType=" + messageType + ", Error=" + error + "]";
	}
}
