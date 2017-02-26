package server.model;

import java.util.ArrayList;
import java.util.Collection;

public class Channel {
	
	private String channelId;
	private Collection<MessageThread> channelThread = new ArrayList<>();
	private Collection<ThreadUser> users = new ArrayList<>();
	private String description;
	private int numberOfSubscribers;
	private boolean isPublic;
	private int unreadMessages;
	private int unreadMentionedMessages;
	
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
	
	public Collection<MessageThread> getChannelThread() {
		return channelThread;
	}
	
	public Collection<ThreadUser> getUsers() {
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
