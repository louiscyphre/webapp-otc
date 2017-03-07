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

import java.util.ArrayList;

/**
 * This class model that represents a message thread: a message and its comments
 * @author Ilia and Michael
 *
 */
public class MessageThread {
	
	private Message message; // the message itself
	private ArrayList<MessageThread> replies; // the replies to the message

	public MessageThread(Message message) {
		this.message = message;
		this.replies = new ArrayList<>();
	}

	/**
	 * @return the Message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	public void addReply(MessageThread reply) {
		if (reply != null)
			replies.add(reply);
	}
	
	public ArrayList<MessageThread> getReplies() {
		return replies;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageThread [Message=" + message + ", Replies=" + replies + "]";
	}
}
