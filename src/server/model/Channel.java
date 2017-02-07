package server.model;

import java.util.ArrayList;
import java.util.Collection;

public class Channel {
	
	private String ChannelName;
	private Collection<MessageThread> ChannelThread = new ArrayList<>();
	private Collection<ThreadUser> Users = new ArrayList<>();
	private String Description;
	private int NumberOfSubscribers;
	private boolean IsPublic;
	private int unreadMessages;
	private int unreadMentionedMessages;
	
	public Channel(String channelName, String description, int numberOfSubscribers, boolean isPublic) {
		this.ChannelName = channelName;
		this.Description = description;
		this.NumberOfSubscribers = numberOfSubscribers;
		this.IsPublic = isPublic;
	}
	
	public Channel(String channelName, String description, int numberOfSubscribers, boolean isPublic, int unreadMessages, int undreadMentionedMessages) {
		this.ChannelName = channelName;
		this.Description = description;
		this.NumberOfSubscribers = numberOfSubscribers;
		this.IsPublic = isPublic;
		this.unreadMessages = unreadMessages;
		this.unreadMentionedMessages = undreadMentionedMessages;
	}

	/**
	 * @return the numberOfSubscribers
	 */
	public int getNumberOfSubscribers() {
		return NumberOfSubscribers;
	}

	/**
	 * @param numberOfSubscribers the numberOfSubscribers to set
	 */
	public void setNumberOfSubscribers(int numberOfSubscribers) {
		this.NumberOfSubscribers = numberOfSubscribers;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return ChannelName;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.ChannelName = channelName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.Description = description;
	}

	/**
	 * @return the isPublic
	 */
	public boolean isPublic() {
		return IsPublic;
	}

	/**
	 * @param isPublic the isPublic to set
	 */
	public void setPublic(boolean isPublic) {
		this.IsPublic = isPublic;
	}
	
	public void addUser(ThreadUser user) {
		Users.add(user);
	}
	
	public void addMessage(MessageThread message) {
		ChannelThread.add(message);
	}
	
	public Collection<MessageThread> getChannelThread() {
		return ChannelThread;
	}
	
	public Collection<ThreadUser> getUsers() {
		return Users;
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
		return "Channel [numberOfSubscribers=" + NumberOfSubscribers + ", channelName=" + ChannelName
				+ ", description=" + Description + ", isPublic=" + IsPublic + "]";
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
	    return this.ChannelName.equals(other.ChannelName);
	}

	public String stringify() {
		return ChannelName + " " + Description;
	}
}
