package server.messages;

public class ChannelSuccess {
	
	private String MessageType;
	
	public ChannelSuccess() {
		this.MessageType = "ChannelSuccess";
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthFailure [MessageType=" + MessageType + "]";
	}
}
