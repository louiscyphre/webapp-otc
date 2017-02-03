package server.messages;

import server.model.Message;

public class IncomingMessage {
	
	private String MessageType;
	private Message Message;
	
	public IncomingMessage(Message message) {
		this.MessageType = "IncomingMessage";
		this.Message = message;
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
		return "IncomingMessage [MessageType=" + MessageType + ", Message=" + Message + "]";
	}
}
