package server;

/**
 * A simple place to hold global application constants
 */
public interface AppConstants {

	//derby constants
	public final String DB_NAME = "DB_NAME";
	public final String DB_DATASOURCE = "DB_DATASOURCE";
	public final String DB_CONTEXT = "java:comp/env/jdbc/ExampleDatasource";
	public final String PROTOCOL = "jdbc:derby:"; 
	public final String OPEN = "Open";
	public final String SHUTDOWN = "Shutdown";
	// message properties
	public final String MESSAGE_PROPERTY = "MessageType";
	public final String MESSAGE_CONTENT = "MessageContent";
	// message types
	public final String MESSAGE_CREATE_CHANNEL = "CreateChannel";
	public final String MESSAGE_SUBSCRIBE = "Subscribe";
	public final String MESSAGE_UNSUBSCRIBE = "Unsubscribe";
	public final String MESSAGE_CHANNEL_DISCOVERY = "ChannelDiscovery";
	public final String MESSAGE_CHANNEL_VIEWING = "ChannelViewing";
	public final String MESSAGE_RECEIVED_MESSAGE = "MessageReceived";
	
	public final String USERS = "users";
	public final String USERS_FILE = USERS + ".json";
	// newsql statements
	public final String CREATE_USER_TABLE = "CREATE TABLE USERS("
			+ "Username varchar(10),"
			+ "Password varchar(32),"
			+ "Nickname varchar(20),"
			+ "Description varchar(50),"
			+ "AvatarUrl varchar(500),"
			+ "PRIMARY KEY(Username))";
	public final String INSERT_USER_STMT = "INSERT INTO " + USERS + " VALUES(?,?,?,?,?)";
	public final String SELECT_USER_BY_USERNAME_STMT = "SELECT * FROM " + USERS + " WHERE Username=?";
	public final String SELECT_USER_BY_CREDENTIALS_STMT = "SELECT * FROM USERS WHERE Username=? AND Password=?";
	public final String SELECT_USER_BY_NICKNAME_LIKENESS_STMT = "SELECT * FROM " + USERS + " WHERE Nickname LIKE ?";
	public final String SELECT_USERS = "SELECT * FROM USERS";
	
	public final String CHANNELS = "channels";
	public final String CHANNELS_FILE = CHANNELS + ".json";
	public final String CREATE_CHANNELS_TABLE = "CREATE TABLE " + CHANNELS + "("
			+ "ChannelName varchar(30),"
			+ "Description varchar(500),"
			+ "NumberOfSubscribers integer,"
			+ "IsPublic boolean,"
			+ "PRIMARY KEY(ChannelName))";
	public final String INSERT_CHANNEL_STMT = "INSERT INTO " + CHANNELS + " VALUES(?,?,?,?)";
	public final String SELECT_CHANNEL_BY_CHANNELNAME_STMT = "SELECT * FROM " + CHANNELS + " WHERE ChannelName=?";
	public final String SELECT_CHANNEL_BY_CHANNELNAME_LIKENESS_STMT = "SELECT * FROM " + CHANNELS + " WHERE ChannelName LIKE ?";
	public final String SELECT_CHANNELS = "SELECT * FROM " + CHANNELS;
	public final String UPDATE_CHANNEL_SUBSCRIBERS_COUNT_STMT = "UPDATE " + CHANNELS + " SET NumberOfSubscribers=? WHERE ChannelName=?";
	
	public final String SUBSCRIPTIONS = "subscriptions";
	public final String SUBSCRIPTIONS_FILE = SUBSCRIPTIONS + ".json";
	public final String CREATE_SUBSCRIPTION_TABLE = "CREATE TABLE " + SUBSCRIPTIONS + "("
			+ "ChannelId varchar(30),"
			+ "UserId varchar(10),"
			+ "SubscriptionTime timestamp,"
			+ "IsViewing boolean,"
			+ "UnreadMessages integer,"
			+ "UnreadMentionedMessages integer,"
			+ "FOREIGN KEY(UserId) REFERENCES " + USERS + "(Username),"
			+ "FOREIGN KEY(ChannelId) REFERENCES " + CHANNELS + "(ChannelName))";
	public final String INSERT_SUBSCRIPTION_STMT = "INSERT INTO " + SUBSCRIPTIONS + " VALUES(?,?,?,?,?,?)";
	public final String SELECT_SUBSCRIPTIONS_BY_CHANNEL_STMT = "SELECT * FROM " + SUBSCRIPTIONS + " WHERE ChannelID=?";
	public final String SELECT_SUBSCRIPTIONS_BY_USERNAME_STMT = "SELECT * FROM " + SUBSCRIPTIONS + " WHERE UserID=?";
	public final String SELECT_SUBSCRIPTIONS_BY_CHANNEL_AND_USER_STMT = "SELECT * FROM " + SUBSCRIPTIONS + " WHERE ChannelID=? AND UserId=?";
	public final String SELECT_SUBSCRIPTIONS = "SELECT * FROM " + SUBSCRIPTIONS;
	public final String REMOVE_SUBSCRIPTION_BY_CHANNEL_AND_USER_STMT = "DELETE FROM " + SUBSCRIPTIONS + " WHERE ChannelID=? AND UserId=?";
	public final String UPDATE_SUBSCRIPTION_STMT = "UPDATE " + SUBSCRIPTIONS + " IsViewing=?, UnreadMessages=?, UnreadMentionedMessages=? WHERE ChannelId=? AND UserId=?";
	
	public final String MESSAGES = "messages";
	public final String MESSAGES_FILE = MESSAGES + ".json";
	public final String CREATE_MESSAGES_TABLE = "CREATE TABLE " + MESSAGES + "("
			+ "ID integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
			+ "ChannelId varchar(30),"
			+ "UserId varchar(10),"
			+ "MessageTime timestamp,"
			+ "LastModified timestamp,"
			+ "RepliedToId integer,"
			+ "Content varchar(500),"
			+ "PRIMARY KEY(ID),"
			+ "FOREIGN KEY(UserId) REFERENCES " + USERS + "(Username),"
			+ "FOREIGN KEY(ChannelId) REFERENCES " + CHANNELS + "(ChannelName))";
	public final String INSERT_MESSAGE_STMT = "INSERT INTO " + MESSAGES
			+ " (ChannelId, UserId, MessageTime, LastModified, RepliedToId, Content) VALUES(?,?,?,?,?,?)";
	public final String SELECT_MESSAGES_BY_CHANNEL_STMT = "SELECT * FROM " + MESSAGES + " WHERE ChannelId=? ORDER BY LastModified, MessageTime";
	public final String SELECT_MESSAGES = "SELECT * FROM " + MESSAGES;
}
