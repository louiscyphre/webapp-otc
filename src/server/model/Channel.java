package server.model;

import java.util.ArrayList;
import java.util.Collection;

public class Channel {
	
	private String Name;
	private Collection<MessageThread> ChannelThread = new ArrayList<>();
	private Collection<ThreadUser> Users = new ArrayList<>();
	private String Description;
	private int SubscribersCount;
	private transient boolean isPublic;
	
	public Channel(String channelName, String description, int numberOfSubscribers, boolean isPublic) {
		this.Name = channelName;
		this.Description = description;
		this.SubscribersCount = numberOfSubscribers;
		this.isPublic = isPublic;
	}

	/**
	 * @return the numberOfSubscribers
	 */
	public int getNumberOfSubscribers() {
		return SubscribersCount;
	}

	/**
	 * @param numberOfSubscribers the numberOfSubscribers to set
	 */
	public void setNumberOfSubscribers(int numberOfSubscribers) {
		this.SubscribersCount = numberOfSubscribers;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return Name;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.Name = channelName;
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
		return isPublic;
	}

	/**
	 * @param isPublic the isPublic to set
	 */
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
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

	@Override
	public String toString() {
		return "Channel [numberOfSubscribers=" + SubscribersCount + ", channelName=" + Name
				+ ", description=" + Description + ", isPublic=" + isPublic + "]";
	}

	public String stringify() {
		return Name + " " + Description;
	}
}
