package server.messages;

/**
 * This message is sent to the client whenver a message is sent in a channel the user is not viewing
 * @author Ilia and Michael
 *
 */
public class UpdateCountersMessage {
	
	private String messageType = "updateCounters"; // the message type
	private String channelId; // the channel's name
	private int unreadMessages; // number of unread messages
	private int unreadMentionedMessages; // number of unread messages that mention the user's nickname
	
	public UpdateCountersMessage(String channelId, int unreadMessages, int unreadMentionedMessages) {
		this.channelId = channelId;
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
	 * @return the channelId
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
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
}
