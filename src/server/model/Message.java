package server.model;

import java.sql.Timestamp;

public class Message {
	private int Id;
	private String ChannelId;
	private String UserId = null;
	private ThreadUser User;
	private Timestamp MessageTime;
	private Timestamp LastModified;
	private int RepliedToId;
	private String Content;

	public Message(int id, String channelId, String user, Timestamp messageTime, int repliedToId, String content) {
		this.Id = id;
		this.ChannelId = channelId;
		this.UserId = user;
		this.MessageTime = this.LastModified = messageTime;
		this.RepliedToId = repliedToId;
		this.Content = content;
	}

	public Message(int id, String channelId, String user, Timestamp messageTime, Timestamp lastModified, int repliedToId, String content) {
		this.Id = id;
		this.ChannelId = channelId;
		this.UserId = user;
		this.MessageTime = messageTime;
		this.LastModified = lastModified;
		this.RepliedToId = repliedToId;
		this.Content = content;
	}

	public Message(int id, String channelId, ThreadUser user, Timestamp messageTime, Timestamp lastModified, int repliedToId, String content) {
		this.Id = id;
		this.ChannelId = channelId;
		this.User = user;
		this.MessageTime = messageTime;
		this.LastModified = lastModified;
		this.RepliedToId = repliedToId;
		this.Content = content;
	}

	/**
	 * @return the lastModified
	 */
	public Timestamp getLastModified() {
		return LastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Timestamp lastModified) {
		LastModified = lastModified;
	}

	public Message(int id, String channelId, ThreadUser user, Timestamp messageTime, int repliedToId, String content) {
		this.Id = id;
		this.ChannelId = channelId;
		this.User = user;
		this.UserId = user.getUsername();
		this.MessageTime = messageTime;
		this.RepliedToId = repliedToId;
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
		return ChannelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.ChannelId = channelId;
	}

	/**
	 * @return the repliedToId
	 */
	public int getRepliedToId() {
		return RepliedToId;
	}

	/**
	 * @param repliedToId the repliedToId to set
	 */
	public void setRepliedToId(int repliedToId) {
		this.RepliedToId = repliedToId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return UserId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String user) {
		this.UserId = user;
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
		return "Message [id=" + Id + ", channelId=" + ChannelId + ", repliedToId=" + RepliedToId + ", User=" + UserId
				+ ", content=" + Content + ", messageTime=" + MessageTime + "]";
	}
	
	public static Message getMessageByCredentials(int id, MessageCredentials credentials, ThreadUser user, Timestamp messageTime) {
		return new Message(id, credentials.getChannel(), user, messageTime, credentials.getReplyToID(), credentials.getContent());
	}
}
