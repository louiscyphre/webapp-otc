package server.model;

public class MessageCredentials {
	private String Channel;
	private String Username;
	private int ReplyToID;
	private String Content;

	public MessageCredentials(String channelId, String username, int repliedToId, String content) {
		this.Channel = channelId;
		this.Username = username;
		this.ReplyToID = repliedToId;
		this.Content = content;
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

	/**
	 * @return the username
	 */
	public String getUsername() {
		return Username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		Username = username;
	}

	/**
	 * @return the replyToID
	 */
	public int getReplyToID() {
		return ReplyToID;
	}

	/**
	 * @param replyToID the replyToID to set
	 */
	public void setReplyToID(int replyToID) {
		ReplyToID = replyToID;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return Content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		Content = content;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageCredentials [Channel=" + Channel + ", Username=" + Username + ", ReplyToID=" + ReplyToID
				+ ", Content=" + Content + "]";
	}
}
