package server.model;

import java.sql.Timestamp;

public class Subscription {
	
	private String channelId;
	private String userId;
	private Timestamp subscriptionTime;
	private int lastReadMessageId;
	private int unreadMessages;
	private int unreadMentionedMessages;

	public Subscription(String channelId, String userId, Timestamp subscriptionTime, int lastReadMessageId, int unreadMessages, int undreadMentionedMessages) {
		this.channelId = channelId;
		this.userId = userId;
		this.subscriptionTime = subscriptionTime;
		this.lastReadMessageId = lastReadMessageId;
		this.unreadMessages = unreadMessages;
		this.unreadMentionedMessages = undreadMentionedMessages;
	}

	public Subscription(String channelId, String userID) {
		this.channelId = channelId;
		this.userId = userID;
	}

	/**
	 * @return the channelId
	 */
	public String getChannelName() {
		return channelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	/**
	 * @return the userId
	 */
	public String getUsername() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUsername(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the subscriptionTime
	 */
	public Timestamp getSubscriptionTime() {
		return subscriptionTime;
	}

	/**
	 * @param subscriptionTime the subscriptionTime to set
	 */
	public void setSubscriptionTime(Timestamp subscriptionTime) {
		this.subscriptionTime = subscriptionTime;
	}

	/**
	 * @return the lastReadMessageId
	 */
	public int getLastReadMessageId() {
		return lastReadMessageId;
	}

	/**
	 * @param lastReadMessageId the lastReadMessageId to set
	 */
	public void setLastReadMessageId(int lastReadMessageId) {
		this.lastReadMessageId = lastReadMessageId;
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
		return "Subscription [ChannelId=" + channelId + ", UserId=" + userId + ", SubscriptionTime=" + subscriptionTime
				+ "]";
	}
}
