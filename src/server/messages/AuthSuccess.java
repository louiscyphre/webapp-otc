package server.messages;

import java.util.ArrayList;
import java.util.Collection;

import server.model.Channel;
import server.model.ThreadUser;

/**
 * This message is sent to the client on a login/registration success
 * @author Ilia and Michael
 *
 */
public class AuthSuccess {
	
	private String messageType = "authSuccess"; // the message type
	private ThreadUser user; // the user details (username, nickname, description and avatar)
	private Collection<Channel> subscribedChannels; // list of the public channels to which the user is subscribed
	private Collection<Channel> privateChannels; // list of the private channels to which the user is subscribed
	
	public AuthSuccess(ThreadUser user) {
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
