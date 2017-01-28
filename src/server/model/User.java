package server.model;

import server.util.Hash;

import java.security.NoSuchAlgorithmException;

/**
 * A simple bean to hold data
 */
public class User {

	private String id, Username, PasswordHash, Nickname, Description, AvatarUrl;

	public User(String userId, String userName, String hashPassword, String nickname, String description, String avatarUrl) throws NoSuchAlgorithmException {
		//idHash = Hash.getSha256Hex(userName);
		id = userId;
		Username = userName;
		PasswordHash = hashPassword;
		setNickname(nickname);
		Description = description;
		AvatarUrl = avatarUrl;
	}

	/**
	 * @return
	 */
	public String getIdHash() {
    	return id;
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

	public void setId(String hash) {
		id = hash;
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
