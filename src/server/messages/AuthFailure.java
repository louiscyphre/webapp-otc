package server.messages;

/**
 * This message is sent to the client whenever login/registration fails
 * @author Ilia and Michael
 *
 */
public class AuthFailure {
	
	private String messageType = "authFailure"; // the type of message
	private String error; // the error that occured
	
	public AuthFailure(String error) {
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
		return "AuthFailure [MessageType=" + messageType + ", Error=" + error + "]";
	}
}
