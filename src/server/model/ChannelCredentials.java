package server.model;


/**
 * Class used for check users login credentials before they enter the chat
 * @author Michael
 *
 */
public class ChannelCredentials {
	
	private String owner;
	private String name; 
	private String description;
	private	String username = null;
	
	public ChannelCredentials(String owner, String channelName, String description, String username) {
		this.owner = owner;
		this.name = channelName;
		this.description = description;
		this.username = username;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
		return "ChannelCredentials [Name=" + name + ", Description=" + description + ", Username=" + username + "]";
	}
}