package server.messages;

import server.model.Message;

public class IncomingMessage {
	
	private String MessageType;
	private String Channel;
	private Message Message;
	private int unreadMessages;
	private int unreadMentionedMessages;
	
	public IncomingMessage(String channel, Message message, int unreadMessages, int unreadMentionedMessages) {
		this.Channel = channel;
		this.MessageType = "IncomingMessage";
		this.Message = message;
		this.unreadMessages = unreadMessages;
		this.unreadMentionedMessages = unreadMentionedMessages;
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
	 * @return the message
	 */
	public Message getMessage() {
		return Message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(Message message) {
		Message = message;
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
		return "IncomingMessage [MessageType=" + MessageType + ", Message=" + Message + "]";
	}
}
