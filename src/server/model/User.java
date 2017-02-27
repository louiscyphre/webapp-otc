package server.model;

/**
 * A model that represents a user
 * @author Ilia and Michael
 *
 */
public class User {

	private String username; // the user's username
	private String password; // the user's password
	private String nickname; // the user's nickname
	private String description; // the user's description
	private String avatarUrl; // url to the user's avatar

	public User(String userName, String password, String nickname, String description, String avatarUrl) {
		this.username = userName;
		this.password = password;
		setNickname(nickname);
		this.description = description;
		this.avatarUrl = avatarUrl;
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

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		if (nickname == null || nickname.equals("")) {
			this.nickname = username;
		} else {
			this.nickname = nickname;
		}
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
	 * @return the avatarUrl
	 */
	public String getAvatarUrl() {
		return avatarUrl;
	}

	/**
	 * @param avatarUrl the avatarUrl to set
	 */
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", nickname=" + nickname + ", description="
				+ description + ", avatarUrl=" + avatarUrl + "]";
	}
}
