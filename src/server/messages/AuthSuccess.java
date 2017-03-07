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
