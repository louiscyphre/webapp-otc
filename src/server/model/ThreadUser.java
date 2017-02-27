package server.model;

/**
 * The model that represents a light-weight user (user without password)
 * @author Ilia and Michael
 *
 */
public class ThreadUser {

	private String username;
	private String nickname;
	private String description;
	private String avatarUrl;

	public ThreadUser(String username, String nickname, String description, String avatarUrl) {
		this.username = username;
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
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
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
	
	/**
	 * Creates a ThreadUser with a User's credentials
	 * @param user the user whose credentials to be inserted into the ThreadUser
	 * @return returns the light-weight thread-user corresponding to the given user
	 */
	public static ThreadUser getThreadUserByUser(User user) {
		if (user == null) {
			return null;
		}
		return new ThreadUser(user.getUsername(), user.getNickname(), user.getDescription(), user.getAvatarUrl());
	}
}
