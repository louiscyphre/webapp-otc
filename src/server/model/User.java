package server.model;

/**
 * A simple bean to hold data
 */
public class User {

	private String Username;
	private String PasswordHash;
	private String Nickname;
	private String Description;
	private String AvatarUrl;

	public User(String userName, String hashPassword, String nickname, String description, String avatarUrl) {
		//idHash = Hash.getSha256Hex(userName);
		Username = userName;
		PasswordHash = hashPassword;
		setNickname(nickname);
		Description = description;
		AvatarUrl = avatarUrl;
	}

	public String getUsername() {  return Username; }
	
	public String getPasswordHash() {
		return PasswordHash;
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

	public void setUsername(String userName) {
		Username = userName;
	}
	public void setPasswordHash(String hash) {
		PasswordHash = hash;
	}

	public void setNickname(String nickname) {
		if (nickname == null || nickname.equals("")) {
			Nickname = Username;
		} else {
			Nickname = nickname;
		}
	}

	public void setDescription(String description) {
		Description = description;
	}

	public void setAvatarUrl(String avatarUrl) {
		AvatarUrl = avatarUrl;
	}
	
	public String stringify() {
	  return Username + " " + PasswordHash;
	}
}
