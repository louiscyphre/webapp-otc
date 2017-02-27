package server.model;

import java.sql.Timestamp;

/**
 * Model of a message as it is stored in the database
 * @author Ilia and Michael
 *
 */
public class MessageDB {
	
	private int id; // message id number
	private String channelId; // channel name (of the channel where the message is posted)
	private String userId = null; // username of the author of the message
	private ThreadUser user; // user details of the author of the message
	private Timestamp messageTime; // the time when the message was posted (in milliseconds)
	private Timestamp lastModified; // the time the message was last modified (in milliseconds)
	private int repliedToId; // if this message is a reply to another message, then that message's id number. otherwise, -1
	private String content; // the content of the message

	public MessageDB(int id, String channelId, String user, Timestamp messageTime, int repliedToId, String content) {
		this.id = id;
		this.channelId = channelId;
		this.userId = user;
		this.messageTime = this.lastModified = messageTime;
		this.repliedToId = repliedToId;
		this.content = content;
	}

	public MessageDB(int id, String channelId, String user, Timestamp messageTime, Timestamp lastModified, int repliedToId, String content) {
		this.id = id;
		this.channelId = channelId;
		this.userId = user;
		this.messageTime = messageTime;
		this.lastModified = lastModified;
		this.repliedToId = repliedToId;
		this.content = content;
	}

	public MessageDB(int id, String channelId, ThreadUser user, Timestamp messageTime, Timestamp lastModified, int repliedToId, String content) {
		this.id = id;
		this.channelId = channelId;
		this.user = user;
		this.messageTime = messageTime;
		this.lastModified = lastModified;
		this.repliedToId = repliedToId;
		this.content = content;
	}

	/**
	 * @return the lastModified
	 */
	public Timestamp getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}

	public MessageDB(int id, String channelId, ThreadUser user, Timestamp messageTime, int repliedToId, String content) {
		this.id = id;
		this.channelId = channelId;
		this.user = user;
		this.userId = user.getUsername();
		this.messageTime = messageTime;
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
	 * @param user the userId to set
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
	 * @param user the userId to set
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
		return messageTime;
	}

	/**
	 * @param messageTime the messageTime to set
	 */
	public void setMessageTime(Timestamp messageTime) {
		this.messageTime = messageTime;
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
