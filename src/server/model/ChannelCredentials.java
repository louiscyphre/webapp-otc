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


/**
 * Class used for check channel credentials before creation
 * @author Ilia and Michael
 *
 */
public class ChannelCredentials {
	
	private String owner; // nickname of the user who wants to create the channel
	private String channelId; // channel name
	private String description; // channel description
	private	String username = null; // username of a second user (if it's a private channel between the two users)
	
	public ChannelCredentials(String owner, String channelName, String description, String username) {
		this.owner = owner;
		this.channelId = channelName;
		this.description = description;
		this.username = username;
	}
	
	/**
	 * @return the creator of the channel
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * @param owner username of the user that created the channel
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return channelId;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.channelId = name;
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelCredentials [Name=" + channelId + ", Description=" + description + ", Username=" + username + "]";
	}
}