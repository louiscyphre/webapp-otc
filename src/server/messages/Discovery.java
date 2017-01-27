package server.messages;

import java.util.Collection;

import server.model.Channel;

public class Discovery {
	
	private String MessageType;
	private Collection<Channel> Channels;
	
	public Discovery(Collection<Channel> channels) {
		this.MessageType = "ChannelDiscovery";
		this.Channels = channels; 
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
	 * @return the channels
	 */
	public Collection<Channel> getChannels() {
		return Channels;
	}

	/**
	 * @param channels the channels to set
	 */
	public void setChannels(Collection<Channel> channels) {
		Channels = channels;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Discovery [MessageType=" + MessageType + ", Channels=" + Channels + "]";
	}
}
