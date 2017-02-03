package server.model;


public class ChannelViewing {
	private String channel;
	private boolean isViewing;
	
	public ChannelViewing(String channel, boolean isViewing) {
		this.channel = channel;
		this.isViewing = isViewing;
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

	/**
	 * @return the isViewing
	 */
	public boolean isViewing() {
		return isViewing;
	}

	/**
	 * @param isViewing the isViewing to set
	 */
	public void setViewing(boolean isViewing) {
		this.isViewing = isViewing;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelViewing [channel=" + channel + ", isViewing=" + isViewing + "]";
	}
}