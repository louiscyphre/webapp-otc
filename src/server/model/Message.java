package server.model;

import java.sql.Timestamp;

public class Message {
	
	private int id;
	private String channelId;
	private String userId = null;
	private ThreadUser user;
	private long messageTime;
	private long lastModified;
	private int repliedToId;
	private String content;

	public Message(int id, String channelId, String user, Timestamp messageTime, int repliedToId, String content) {
		this.id = id;
		this.channelId = channelId;
		this.userId = user;
		this.messageTime = this.lastModified = messageTime.getTime();
		this.repliedToId = repliedToId;
		this.content = content;
	}

	public Message(int id, String channelId, String user, Timestamp messageTime, Timestamp lastModified, int repliedToId, String content) {
		this.id = id;
		this.channelId = channelId;
		this.userId = user;
		this.messageTime = messageTime.getTime();
		this.lastModified = lastModified.getTime();
		this.repliedToId = repliedToId;
		this.content = content;
	}

	public Message(int id, String channelId, ThreadUser user, Timestamp messageTime, Timestamp lastModified, int repliedToId, String content) {
		this.id = id;
		this.channelId = channelId;
		this.user = user;
		this.messageTime = messageTime.getTime();
		this.lastModified = lastModified.getTime();
		this.repliedToId = repliedToId;
		this.content = content;
	}

	/**
	 * @return the lastModified
	 */
	public Timestamp getLastModified() {
		return new Timestamp(lastModified);
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified.getTime();
	}

	public Message(int id, String channelId, ThreadUser user, Timestamp messageTime, int repliedToId, String content) {
		this.id = id;
		this.channelId = channelId;
		this.user = user;
		this.userId = user.getUsername();
		this.messageTime = messageTime.getTime();
		this.repliedToId = repliedToId;
		this.content = content;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
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
	 * @return the repliedToId
	 */
	public int getRepliedToId() {
		return repliedToId;
	}

	/**
	 * @param repliedToId the repliedToId to set
	 */
	public void setRepliedToId(int repliedToId) {
		this.repliedToId = repliedToId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String user) {
		this.userId = user;
	}

	/**
	 * @return the userId
	 */
	public ThreadUser getUser() {
		return user;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUser(ThreadUser user) {
		this.user = user;
		this.userId = user.getUsername();
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the messageTime
	 */
	public Timestamp getMessageTime() {
		return new Timestamp(messageTime);
	}

	/**
	 * @param messageTime the messageTime to set
	 */
	public void setMessageTime(Timestamp messageTime) {
		this.messageTime = messageTime.getTime();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [id=" + id + ", channelId=" + channelId + ", repliedToId=" + repliedToId + ", User=" + userId
				+ ", content=" + content + ", messageTime=" + messageTime + "]";
	}
}
