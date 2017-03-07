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

import server.model.ThreadUser;

/**
 * This message is sent to all users in a channel to notify them of an addition of a new user to the channel
 * @author Ilia and Michael
 *
 */
public class UserSubscribed {
	
	private String messageType = "userSubscribed"; // the type of message
	private String channelId; // the channel's name
	private ThreadUser user; // user's details (username, nickname, description and avatar)
	
	public UserSubscribed(String channel, ThreadUser user) {
		this.channelId = channel;
		this.user = user;
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channelId;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channelId = channel;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserSubscribed [MessageType=" + messageType + ", Channel=" + channelId + ", User=" + user + "]";
	}
}
