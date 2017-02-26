package server.messages;

import java.util.Collection;

import server.model.MessageThread;

public class DownloadMessages {
	
	private String messageType;
	private String channelId;
	private Collection<MessageThread> channelThread;
	private int unreadMessages;
	private int unreadMentionedMessages;
	
	public DownloadMessages(String channel, Collection<MessageThread> channelThread, int unreadMessages, int unreadMentionedMessages) {
		this.messageType = "downloadMessages";
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
