package server;

import java.lang.reflect.Type;
import java.util.Collection;

import com.google.gson.reflect.TypeToken;

import server.model.Customer;

/**
 * A simple place to hold global application constants
 */
public interface AppConstants {

	//public final String CUSTOMERS = "customers";
	//public final String CUSTOMERS_FILE = CUSTOMERS + ".json";
	//public final String NAME = "name";
	//public final Type CUSTOMER_COLLECTION = new TypeToken<Collection<Customer>>() {}.getType();
	//derby constants
	public final String DB_NAME = "DB_NAME";
	public final String DB_DATASOURCE = "DB_DATASOURCE";
	public final String PROTOCOL = "jdbc:derby:"; 
	public final String OPEN = "Open";
	public final String SHUTDOWN = "Shutdown";
	
	//sql statements
	//public final String CREATE_CUSTOMERS_TABLE = "CREATE TABLE CUSTOMER(Name varchar(100),"
	//		+ "City varchar(100),"
	//		+ "Country varchar(100))";
	//public final String INSERT_CUSTOMER_STMT = "INSERT INTO CUSTOMER VALUES(?,?,?)";
	//public final String SELECT_ALL_CUSTOMERS_STMT = "SELECT * FROM CUSTOMER";
	//public final String SELECT_CUSTOMER_BY_NAME_STMT = "SELECT * FROM CUSTOMER "
	//		+ "WHERE Name=?";

	public final String USERS = "users";
	public final String USERS_FILE = USERS + ".json";
	// newsql statements
	public final String CREATE_USER_TABLE = "CREATE TABLE USERS(id varchar(10),"
			+ "Username varchar(10),"
			+ "PasswordHash varchar(32),"
			+ "Nickname varchar(20),"
			+ "Description varchar(50),"
			+ "AvatarUrl varchar(500), PRIMARY KEY(id), UNIQUE(id, Username))";
	public final String INSERT_USER_STMT = "INSERT INTO USERS VALUES(?,?,?,?,?,?)";
	public final String SELECT_USER_BY_USERNAME_STMT = "SELECT * FROM USERS "
			+ "WHERE Username=?";
	public final String SELECT_USER_BY_CREDENTIALS_STMT = "SELECT * FROM USERS WHERE Username=? AND PasswordHash=?";
	public final String SELECT_USERS = "SELECT * FROM USERS";
}
