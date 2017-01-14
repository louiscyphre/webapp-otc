package server.model;

import server.util.Hash;

import java.security.NoSuchAlgorithmException;

/**
 * A simple bean to hold data
 */
public class User {

	private String idHash, username, passwordHash, Nickname, Description, AvatarUrl;

	public User(String userId, String userName, String hashPassword, String nickname, String description, String avatarUrl) throws NoSuchAlgorithmException {
		//idHash = Hash.getSha256Hex(userName);
		idHash = userId;
		username = userName;
		passwordHash = hashPassword;
		setNickname(nickname);
		Description = description;
		AvatarUrl = avatarUrl;
	}

	/**
	 * @return
	 */
	public String getIdHash() {
    	return idHash;
    }
	public String getUsername() {  return username; }
	
	public String getPasswordHash() {
		return passwordHash;
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
		idHash = hash;
	}

	public void setUsername(String userName) {
		username = userName;
	}
	public void setPasswordHash(String hash) {
		passwordHash = hash;
	}

	public void setNickname(String nickname) {
		if (nickname == null || nickname.equals("")) {
			Nickname = username;
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
}
