package server.model;

import java.util.ArrayList;
import java.util.Collection;

public class MessageThread {
	
	private Message message;
	private Collection<MessageThread> replies;

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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageThread [Message=" + message + ", Replies=" + replies + "]";
	}
}
