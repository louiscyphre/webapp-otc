package server.messages;

public class ChannelSuccess {
	
	private String MessageType;
	private String ChannelName = null;
	
	public ChannelSuccess() {
		this.MessageType = "ChannelSuccess";
	}
	
	public ChannelSuccess(String channelName) {
		this.MessageType = "ChannelSuccess";
		this.ChannelName = channelName;
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
	 * @return the channelName
	 */
	public String getChannelName() {
		return ChannelName;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		ChannelName = channelName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelSuccess [MessageType=" + MessageType + ", ChannelName=" + ChannelName + "]";
	}
}
