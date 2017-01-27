package server.model;

import java.util.ArrayList;
import java.util.Collection;

public class MessageThread {
	private Message Message;
	private Collection<MessageThread> Replies;

	public MessageThread(Message message) {
		this.Message = message;
		this.Replies = new ArrayList<>();
	}

	/**
	 * @return the Message
	 */
	public Message getMessage() {
		return Message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(Message message) {
		this.Message = message;
	}

	public void addReply(MessageThread reply) {
		if (reply != null)
			Replies.add(reply);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageThread [Message=" + Message + ", Replies=" + Replies + "]";
	}
}
