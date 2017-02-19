package server.model;

public class MessageCredentials {
	private Message Message;

	public MessageCredentials(Message message) {
		this.Message = message;
	}

	/**
	 * @return the message
	 */
	public Message getMessage() {
		return Message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(Message message) {
		Message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageCredentials [Message=" + Message + "]";
	}
}
