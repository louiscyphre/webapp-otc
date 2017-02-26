package server.model;

/**
 * A simple bean to hold data
 */
public class User {

	private String username;
	private String password;
	private String nickname;
	private String description;
	private String avatarUrl;

	public User(String userName, String password, String nickname, String description, String avatarUrl) {
		this.username = userName;
		this.password = password;
		setNickname(nickname);
		this.description = description;
		this.avatarUrl = avatarUrl;
	}

	public String getUsername() {  return username; }
	
	public String getPassword() {
		return password;
	}

	/**
	 * @return
	 */
	public String getNickname() {
		return nickname;
	}

	public String getDescription() {
		return description;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setUsername(String userName) {
		username = userName;
	}
	public void setPasswordHash(String hash) {
		password = hash;
	}

	public void setNickname(String nickname) {
		if (nickname == null || nickname.equals("")) {
			this.nickname = username;
		} else {
			this.nickname = nickname;
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	
	public String stringify() {
	  return username + " " + password;
	}
}
