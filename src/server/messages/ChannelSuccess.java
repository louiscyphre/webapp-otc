package server.messages;

import server.model.Channel;

/**
 * The channel that is sent to the client when creating of a channel succeeded
 * @author Ilia and Michael
 *
 */
public class ChannelSuccess {
	
	private String messageType = "channelSuccess"; // the type of message
	private Channel channel; // the channel details (name, description, users, messages and etc.)
	
	public ChannelSuccess(Channel channel) {
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
