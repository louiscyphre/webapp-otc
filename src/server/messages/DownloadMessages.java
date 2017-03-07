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

package server.messages;

import java.util.Collection;

import server.model.MessageThread;

/**
 * This message is sent to the client whenever a request for more thread messages is received
 * @author Ilia and Michael
 *
 */
public class DownloadMessages {
	
	private String messageType = "downloadMessages"; // the type of message
	private String channelId; // the name of the channel which messages are sent
	private Collection<MessageThread> channelThread; // the list of messages that are downloaded this time
	private int unreadMessages; // number of remaining unread messages
	private int unreadMentionedMessages; // number of remaining messages in which the requesting user's nickname is mentioned
	
	public DownloadMessages(String channel, Collection<MessageThread> channelThread, int unreadMessages, int unreadMentionedMessages) {
		this.channelId = channel;
		this.channelThread = channelThread;
		this.unreadMessages = unreadMessages;
		this.unreadMentionedMessages = unreadMentionedMessages;
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
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

	/**
	 * @return the channelThread
	 */
	public Collection<MessageThread> getChannelThread() {
		return channelThread;
	}

	/**
	 * @param channelThread the channelThread to set
	 */
	public void setChannelThread(Collection<MessageThread> channelThread) {
		this.channelThread = channelThread;
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
		return "DownloadMessages [MessageType=" + messageType + ", Channel=" + channelId + ", ChannelThread="
				+ channelThread + "]";
	}
}
