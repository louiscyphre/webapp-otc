package server.messages;

import server.model.Channel;

public class ChannelSuccess {
	
	private String MessageType;
	private Channel Channel;
	
	public ChannelSuccess(Channel channel) {
		this.MessageType = "ChannelSuccess";
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
	
	@Override
	public String toString() {
		return "ChannelSuccess [MessageType=" + MessageType + ", Channel=" + Channel + "]";
	}
}
