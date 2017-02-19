package server.model;

public class ChannelDownloadedMessages {
	private Channel channel;
	private int downloadedMessages;
	
	public ChannelDownloadedMessages(Channel channel) {
		this.channel = channel;
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

	/**
	 * @return the downloadedMessages
	 */
	public int getDownloadedMessages() {
		return downloadedMessages;
	}

	/**
	 * @param downloadedMessages the downloadedMessages to set
	 */
	public void setDownloadedMessages(int downloadedMessages) {
		this.downloadedMessages = downloadedMessages;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelDownloadedMessages [channel=" + channel + ", downloadedMessages=" + downloadedMessages + "]";
	}
}
