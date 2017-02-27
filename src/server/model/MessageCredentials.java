package server.model;

/**
 * Represents a model of a message sent to the server with a newly posted message
 * @author Ilia and Michael
 *
 */
public class MessageCredentials {
	
	private Message message; // the message itself

	public MessageCredentials(Message message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageCredentials [Message=" + message + "]";
	}
}
