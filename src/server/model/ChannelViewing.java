package server.model;

/**
 * Represents a message of a client informing the server which channel the user is viewing
 * @author Ilia and Michael
 *
 */
public class ChannelViewing {
	
	private String channelId; // the channel name
	
	public ChannelViewing(String channel) {
		this.channelId = channel;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channelId;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channelId = channel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelViewing [channel=" + channelId + "]";
	}
}