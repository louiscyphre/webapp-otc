package server.messages;

/**
 * This message is sent to the client when a new message is successfully received
 * @author Ilia and Michael
 *
 */
public class MessageReceived {
	
	private String messageType = "messageReceived"; // the type of message
	private String error = null; // the error that happened (or null if everything is fine)
	
	public MessageReceived() {
	}
	
	public MessageReceived(String error) {
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
