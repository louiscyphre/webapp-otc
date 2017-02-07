package server.model;

/**
 * A simple bean to hold data
 */
public class ThreadUser {

	private String Username;
	private String Nickname;
	private String Description;
	private String AvatarUrl;

	public ThreadUser(String username, String nickname, String description, String avatarUrl) {
		Username = username;
		setNickname(nickname);
		Description = description;
		AvatarUrl = avatarUrl;
	}
	
	public String getUsername() {
		return Username;
	}
	
	public void setUsername(String username) {
		Username = username;
	}

	/**
	 * @return
	 */
	public String getNickname() {
		return Nickname;
	}
	
	public String getDescription() {
		return Description;
	}

	public String getAvatarUrl() {
		return AvatarUrl;
	}

	public void setNickname(String nickname) {
		Nickname = nickname;
	}
	
	public void setDescription(String description) {
		Description = description;
	}

	public void setAvatarUrl(String avatarUrl) {
		AvatarUrl = avatarUrl;
	}
	
	public String stringify() {
	  return Nickname + " " + AvatarUrl;
	}
	
	public static ThreadUser getThreadUserByUser(User user) {
		return new ThreadUser(user.getUsername(), user.getNickname(), user.getDescription(), user.getAvatarUrl());
	}
}
