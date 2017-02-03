package server.model;

import java.sql.Timestamp;

public class Message {
	private int Id;
	private String Channel;
	private ThreadUser User;
	private Timestamp MessageTime;
	private int ReplyToID;
	private String Content;

	public Message(int id, String channelId, ThreadUser user, Timestamp messageTime, int repliedToId, String content) {
		this.Id = id;
		this.Channel = channelId;
		this.User = user;
		this.MessageTime = messageTime;
		this.ReplyToID = repliedToId;
		this.Content = content;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return Id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.Id = id;
	}

	/**
	 * @return the channelId
	 */
	public String getChannelId() {
		return Channel;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.Channel = channelId;
	}

	/**
	 * @return the repliedToId
	 */
	public int getRepliedToId() {
		return ReplyToID;
	}

	/**
	 * @param repliedToId the repliedToId to set
	 */
	public void setRepliedToId(int repliedToId) {
		this.ReplyToID = repliedToId;
	}

	/**
	 * @return the userId
	 */
	public ThreadUser getUser() {
		return User;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUser(ThreadUser user) {
		this.User = user;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return Content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.Content = content;
	}

	/**
	 * @return the messageTime
	 */
	public Timestamp getMessageTime() {
		return MessageTime;
	}

	/**
	 * @param messageTime the messageTime to set
	 */
	public void setMessageTime(Timestamp messageTime) {
		this.MessageTime = messageTime;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [id=" + Id + ", channelId=" + Channel + ", repliedToId=" + ReplyToID + ", User=" + User
				+ ", content=" + Content + ", messageTime=" + MessageTime + "]";
	}
	
	public static Message getMessageByCredentials(int id, MessageCredentials credentials, ThreadUser user, Timestamp messageTime) {
		return new Message(id, credentials.getChannel(), user, messageTime, credentials.getReplyToID(), credentials.getContent());
	}
}
