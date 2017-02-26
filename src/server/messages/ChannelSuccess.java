package server.messages;

import server.model.Channel;

public class ChannelSuccess {
	
	private String messageType;
	private Channel channel;
	
	public ChannelSuccess(Channel channel) {
		this.messageType = "channelSuccess";
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
	
	@Override
	public String toString() {
		return "ChannelSuccess [MessageType=" + messageType + ", Channel=" + channel + "]";
	}
}
