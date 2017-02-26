package server.model;

public class MessageDownload {
	
	private String channelId;
	
	public MessageDownload(String channel) {
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
		return "MessageDownload [Channel=" + channelId + "]";
	}
}
