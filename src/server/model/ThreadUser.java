package server.model;

/**
 * A simple bean to hold data
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
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
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

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	
	public String stringify() {
	  return nickname + " " + avatarUrl;
	}
	
	public static ThreadUser getThreadUserByUser(User user) {
		return new ThreadUser(user.getUsername(), user.getNickname(), user.getDescription(), user.getAvatarUrl());
	}
}
