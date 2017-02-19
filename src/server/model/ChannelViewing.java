package server.model;


public class ChannelViewing {
	private String channel;
	
	public ChannelViewing(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelViewing [channel=" + channel + "]";
	}
}