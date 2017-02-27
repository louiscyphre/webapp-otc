package server.messages;

/**
 * This message is sent to the user when subscribing into a channel fails
 * @author Ilia and Michael
 *
 */
public class SubscribeFailure {
	
	private String messageType = "subscribeFailure"; // the type of message
	private String error; // the error that occured
	
	public SubscribeFailure(String error) {
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
