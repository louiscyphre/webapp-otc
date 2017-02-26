package server.messages;

import java.util.Collection;

import server.model.Channel;

public class Discovery {
	
	private String messageType;
	private Collection<Channel> channels;
	
	public Discovery(Collection<Channel> channels) {
		this.messageType = "channelDiscovery";
		this.channels = channels; 
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
	 * @return the channels
	 */
	public Collection<Channel> getChannels() {
		return channels;
	}

	/**
	 * @param channels the channels to set
	 */
	public void setChannels(Collection<Channel> channels) {
		this.channels = channels;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Discovery [MessageType=" + messageType + ", Channels=" + channels + "]";
	}
}
