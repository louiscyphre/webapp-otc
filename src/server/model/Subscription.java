package server.model;

public class Subscription {
	
	private String ChannelName;
	private String Username;

	public Subscription(String channelId, String userID) {
		this.ChannelName = channelId;
		this.Username = userID;
	}

	/**
	 * @return the channelId
	 */
	public String getChannelName() {
		return ChannelName;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelName(String channelId) {
		this.ChannelName = channelId;
	}

	/**
	 * @return the userID
	 */
	public String getUsername() {
		return Username;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUsername(String userID) {
		this.Username = userID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Subscription [channelId=" + ChannelName + ", userID=" + Username + "]";
	}
}
