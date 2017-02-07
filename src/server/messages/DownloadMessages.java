package server.messages;

import java.util.Collection;

import server.model.Message;
import server.model.MessageThread;

public class DownloadMessages {
	
	private String MessageType;
	private String Channel;
	private Collection<MessageThread> ChannelThread;
	private int UnreadMessages;
	private int UnreadMentionedMessages;
	
	public DownloadMessages(String channel, Collection<MessageThread> channelThread, int unreadMessages, int unreadMentionedMessages) {
		this.MessageType = "DownloadMessages";
		this.Channel = channel;
		this.ChannelThread = channelThread;
		this.UnreadMessages = unreadMessages;
		this.UnreadMentionedMessages = unreadMentionedMessages;
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return MessageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		MessageType = messageType;
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
	 * @return the channelThread
	 */
	public Collection<MessageThread> getChannelThread() {
		return ChannelThread;
	}

	/**
	 * @param channelThread the channelThread to set
	 */
	public void setChannelThread(Collection<MessageThread> channelThread) {
		ChannelThread = channelThread;
	}

	/**
	 * @return the unreadMessages
	 */
	public int getUnreadMessages() {
		return UnreadMessages;
	}

	/**
	 * @param unreadMessages the unreadMessages to set
	 */
	public void setUnreadMessages(int unreadMessages) {
		UnreadMessages = unreadMessages;
	}

	/**
	 * @return the unreadMentionedMessages
	 */
	public int getUnreadMentionedMessages() {
		return UnreadMentionedMessages;
	}

	/**
	 * @param unreadMentionedMessages the unreadMentionedMessages to set
	 */
	public void setUnreadMentionedMessages(int unreadMentionedMessages) {
		UnreadMentionedMessages = unreadMentionedMessages;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DownloadMessages [MessageType=" + MessageType + ", Channel=" + Channel + ", ChannelThread="
				+ ChannelThread + "]";
	}
}
