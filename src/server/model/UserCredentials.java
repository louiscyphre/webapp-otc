package server.model;


/**
 * Class used for check users login credentials before they enter the chat
 * @author Michael
 *
 */
public class UserCredentials {
	
	private String Username; 
	private	String Password;
	
	/**
	 * 
	 * @param username
	 * @param password
	 */
	public UserCredentials(String username, String password) {
		Username = username;
		Password = password;
	}
	
	public String getUsername() {
		return Username;
	}
	
	public String getPassword() {
		return Password;
	}

	public void setUsername(String username) {
		Username = username;
	}
	
	public void setPassword(String password) {
		Password = password;
	}


}
