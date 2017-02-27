package server.messages;

import java.util.Collection;

import server.model.Channel;

/**
 * The message that is sent to the client whenever a request to search for channels is received
 * @author Ilia and Michael
 *
 */
public class Discovery {
	
	private String messageType = "channelDiscovery"; // the message type
	private Collection<Channel> channels; // the list of channels that meet the search criteria
	
	public Discovery(Collection<Channel> channels) {
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
