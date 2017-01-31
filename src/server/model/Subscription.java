package server.model;

public class Subscription {
	
	private String ChannelId;
	private String UserId;

	public Subscription(String channelId) {
		this.ChannelId = channelId;
	}

	public Subscription(String channelId, String userID) {
		this.ChannelId = channelId;
		this.UserId = userID;
	}

	/**
	 * @return the channelId
	 */
	public String getChannelName() {
		return ChannelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelName(String channelId) {
		this.ChannelId = channelId;
	}

	/**
	 * @return the userID
	 */
	public String getUsername() {
		return UserId;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUsername(String userID) {
		this.UserId = userID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Subscription [channelId=" + ChannelId + ", userID=" + UserId + "]";
	}
}
