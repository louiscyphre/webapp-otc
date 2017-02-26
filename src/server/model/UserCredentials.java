package server.model;


/**
 * Class used for check users login credentials before they enter the chat
 * @author Michael
 *
 */
public class UserCredentials {
	
	private String username; 
	private	String password;
	
	/**
	 * 
	 * @param username
	 * @param password
	 */
	public UserCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}


}
