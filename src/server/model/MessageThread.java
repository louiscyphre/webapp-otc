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
