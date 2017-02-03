package server.model;

import java.sql.Timestamp;

public class Subscription {
	
	private String ChannelId;
	private String UserId;
	private Timestamp SubscriptionTime;
	private boolean isViewing;
	private int unreadMessages;
	private int unreadMentionedMessages;

	public Subscription(String channelId, Timestamp subscriptionTime, boolean isViewing, int unreadMessages, int undreadMentionedMessages) {
		this.ChannelId = channelId;
		this.SubscriptionTime = subscriptionTime;
		this.isViewing = isViewing;
		this.unreadMessages = unreadMessages;
		this.unreadMentionedMessages = undreadMentionedMessages;
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
	public void setChannelId(String channelId) {
		ChannelId = channelId;
	}

	/**
	 * @return the userId
	 */
	public String getUsername() {
		return UserId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUsername(String userId) {
		UserId = userId;
	}

	/**
	 * @return the subscriptionTime
	 */
	public Timestamp getSubscriptionTime() {
		return SubscriptionTime;
	}

	/**
	 * @param subscriptionTime the subscriptionTime to set
	 */
	public void setSubscriptionTime(Timestamp subscriptionTime) {
		SubscriptionTime = subscriptionTime;
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

	/**
	 * @return the unreadMessages
	 */
	public int getUnreadMessages() {
		return unreadMessages;
	}

	/**
	 * @param unreadMessages the unreadMessages to set
	 */
	public void setUnreadMessages(int unreadMessages) {
		this.unreadMessages = unreadMessages;
	}

	/**
	 * @return the unreadMentionedMessages
	 */
	public int getUnreadMentionedMessages() {
		return unreadMentionedMessages;
	}

	/**
	 * @param unreadMentionedMessages the unreadMentionedMessages to set
	 */
	public void setUnreadMentionedMessages(int unreadMentionedMessages) {
		this.unreadMentionedMessages = unreadMentionedMessages;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Subscription [ChannelId=" + ChannelId + ", UserId=" + UserId + ", SubscriptionTime=" + SubscriptionTime
				+ "]";
	}
}
