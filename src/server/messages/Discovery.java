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

import java.util.Collection;

import server.model.Channel;

/**
 * The message that is sent to the client whenever a request to search for channels is received
 * @author Ilia and Michael
 *
 */
public class Discovery {
	
	private String messageType = "channelDiscovery"; // the message type
	private Collection<Channel> channels; // the list of channels that meet the search criteria
	
	public Discovery(Collection<Channel> channels) {
		this.channels = channels; 
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
	 * @return the channels
	 */
	public Collection<Channel> getChannels() {
		return channels;
	}

	/**
	 * @param channels the channels to set
	 */
	public void setChannels(Collection<Channel> channels) {
		this.channels = channels;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Discovery [MessageType=" + messageType + ", Channels=" + channels + "]";
	}
}
