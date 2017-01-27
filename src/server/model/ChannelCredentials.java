package server.model;


/**
 * Class used for check users login credentials before they enter the chat
 * @author Michael
 *
 */
public class ChannelCredentials {
	
	private String Owner;
	private String Name; 
	private String Description;
	private	String Username = null;
	
	public ChannelCredentials(String owner, String channelName, String description, String username) {
		this.Owner = owner;
		this.Name = channelName;
		this.Description = description;
		this.Username = username;
	}
	
	public String getOwner() {
		return Owner;
	}
	
	public void setOwner(String owner) {
		Owner = owner;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		Description = description;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return Username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		Username = username;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelCredentials [Name=" + Name + ", Description=" + Description + ", Username=" + Username + "]";
	}
}