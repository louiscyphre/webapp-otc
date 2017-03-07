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
 * Model to represent a message
 * @author Ilia and Michael
 *
 */
public class Message {
	
	private int id; // message id number
	private String channelId; // channel name (of the channel where the message is posted)
	private String userId = null; // username of the author of the message
	private ThreadUser user; // user details of the author of the message
	private long messageTime; // the time when the message was posted (in milliseconds)
	private long lastModified; // the time the message was last modified (in milliseconds)
	private int repliedToId; // if this message is a reply to another message, then that message's id number. otherwise, -1
	private String content; // the content of the message

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
