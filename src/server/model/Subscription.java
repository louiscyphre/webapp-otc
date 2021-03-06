/*
 *     webapp-otc - an online collaboration tool .
 *     Copyright (C) 2017 Ilia Butvinnik and Michael Goldman
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package server.model;

import java.sql.Timestamp;

/**
 * This model represents a subscription of a user to a channel
 * @author Ilia and Michael
 *
 */
public class Subscription {
	
	private String channelId; // the channel name
	private String userId; // the user's username
	private Timestamp subscriptionTime; // time when the user subscribed to the channel
	private int numberOfReadMessages; // number of messages the user has read
	private int unreadMessages; // number of messages the user still hasn't read
	private int unreadMentionedMessages; // number of messages the user still hasn't read that contain his nickname

	public Subscription(String channelId, String userId, Timestamp subscriptionTime, int numberOfReadMessageId, int unreadMessages, int undreadMentionedMessages) {
		this.channelId = channelId;
		this.userId = userId;
		this.subscriptionTime = subscriptionTime;
		this.numberOfReadMessages = numberOfReadMessageId;
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
	 * @return the numberOfReadMessages
	 */
	public int getNumberOfReadMessages() {
		return numberOfReadMessages;
	}

	/**
	 * @param numberOfReadMessages the number of read messages to set
	 */
	public void setNumberOfReadMessages(int numberOfReadMessages) {
		this.numberOfReadMessages = numberOfReadMessages;
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
