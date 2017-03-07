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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Model to represent a channel
 * @author Ilia and Michael
 *
 */
public class Channel {
	
	private String channelId; // the channel's name
	private ArrayList<MessageThread> channelThread = new ArrayList<>(); // list of messages in the channel
	private ArrayList<ThreadUser> users = new ArrayList<>(); // list of users subscribed to the channel
	private String description; // description of the channel
	private int numberOfSubscribers; // number of users currently in the channel
	private boolean isPublic; // whether the channel is public or private
	private int unreadMessages; // number of unread message in the channel (for a specific user)
	private int unreadMentionedMessages; // number of unread message in the channel (for a specific user) with the mention of the user's nickname
	
	public Channel(String channelName, String description, int numberOfSubscribers, boolean isPublic) {
		this.channelId = channelName;
		this.description = description;
		this.numberOfSubscribers = numberOfSubscribers;
		this.isPublic = isPublic;
	}
	
	public Channel(String channelName, String description, int numberOfSubscribers, boolean isPublic, int unreadMessages, int undreadMentionedMessages) {
		this.channelId = channelName;
		this.description = description;
		this.numberOfSubscribers = numberOfSubscribers;
		this.isPublic = isPublic;
		this.unreadMessages = unreadMessages;
		this.unreadMentionedMessages = undreadMentionedMessages;
	}

	/**
	 * @return the numberOfSubscribers
	 */
	public int getNumberOfSubscribers() {
		return numberOfSubscribers;
	}

	/**
	 * @param numberOfSubscribers the numberOfSubscribers to set
	 */
	public void setNumberOfSubscribers(int numberOfSubscribers) {
		this.numberOfSubscribers = numberOfSubscribers;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelId;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelId = channelName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the isPublic
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * @param isPublic the isPublic to set
	 */
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	public void addUser(ThreadUser user) {
		users.add(user);
	}
	
	public void addMessage(MessageThread message) {
		channelThread.add(message);
	}
	
	public ArrayList<MessageThread> getChannelThread() {
		return channelThread;
	}
	
	public ArrayList<ThreadUser> getUsers() {
		return users;
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

	@Override
	public String toString() {
		return "Channel [numberOfSubscribers=" + numberOfSubscribers + ", channelName=" + channelId
				+ ", description=" + description + ", isPublic=" + isPublic + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }
	    
	    if (!Channel.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    
	    final Channel other = (Channel)obj;
	    return this.channelId.equals(other.channelId);
	}

	public String stringify() {
		return channelId + " " + description;
	}
}
