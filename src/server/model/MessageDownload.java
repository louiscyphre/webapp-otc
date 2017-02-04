package server.model;

public class MessageDownload {
	private String Channel;
	
	public MessageDownload(String channel) {
		this.Channel = channel;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return Channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		Channel = channel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageDownload [Channel=" + Channel + "]";
	}
}
