package server.messages;

import server.model.Channel;

public class SubscribeSuccess {
	
	private String messageType;
	private Channel channel;
	
	public SubscribeSuccess(Channel channel) {
		this.messageType = "subscribeSuccess";
		this.channel = channel;
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
	 * @return the channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubscribeSuccess [MessageType=" + messageType + ", Channel=" + channel + "]";
	}
}
