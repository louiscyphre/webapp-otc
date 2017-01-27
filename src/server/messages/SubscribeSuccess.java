package server.messages;

import server.model.Channel;

public class SubscribeSuccess {
	
	private String MessageType;
	private Channel Channel;
	
	public SubscribeSuccess(Channel channel) {
		this.MessageType = "SubscribeSuccess";
		this.Channel = channel;
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
	 * @return the channel
	 */
	public Channel getChannel() {
		return Channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(Channel channel) {
		Channel = channel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubscribeSuccess [MessageType=" + MessageType + ", Channel=" + Channel + "]";
	}
}
