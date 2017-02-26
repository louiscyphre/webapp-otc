package server.messages;

import java.util.ArrayList;
import java.util.Collection;

import server.model.Channel;
import server.model.ThreadUser;

public class AuthSuccess {
	
	private String messageType;
	private ThreadUser user;
	private Collection<Channel> subscribedChannels;
	private Collection<Channel> privateChannels;
	
	public AuthSuccess(ThreadUser user) {
		this.messageType = "authSuccess";
		this.user = user;
		this.subscribedChannels = new ArrayList<>();
		this.privateChannels = new ArrayList<>();
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @return the user
	 */
	public ThreadUser getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(ThreadUser user) {
		this.user = user;
	}

	public void addSubscribedChannel(Channel channel) {
		if (channel != null)
			subscribedChannels.add(channel);
	}
	
	public void addPrivateChannel(Channel channel) {
		if (channel != null)
			privateChannels.add(channel);
	}
}
