package server.messages;

import java.util.ArrayList;
import java.util.Collection;

import server.model.Channel;
import server.model.ThreadUser;

public class AuthSuccess {
	
	private String MessageType;
	private ThreadUser User;
	private Collection<Channel> PublicChannels;
	private Collection<Channel> SubscribedChannels;
	private Collection<Channel> PrivateChannels;
	
	public AuthSuccess(ThreadUser user) {
		this.MessageType = "AuthSuccess";
		this.User = user;
		this.PublicChannels = new ArrayList<>();
		this.SubscribedChannels = new ArrayList<>();
		this.PrivateChannels = new ArrayList<>();
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return MessageType;
	}

	/**
	 * @return the user
	 */
	public ThreadUser getUser() {
		return User;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(ThreadUser user) {
		User = user;
	}

	public void addPublicChannel(Channel channel) {
		if (channel != null)
			PublicChannels.add(channel);
	}
	
	public void addSubscribedChannel(Channel channel) {
		if (channel != null)
			SubscribedChannels.add(channel);
	}
	
	public void addPrivateChannel(Channel channel) {
		if (channel != null)
			PrivateChannels.add(channel);
	}
}
