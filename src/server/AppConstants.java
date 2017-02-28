package server;

/**
 * A simple place to hold global application constants
 */
public interface AppConstants {

	// default avatar
	public final String DEFAULT_AVATAR = "css/img/person.png";
	// derby constants
	public final String DB_NAME = "projectDB";
	public final String DB_DATASOURCE = "DB_DATASOURCE";
	public final String DB_CONTEXT = "java:comp/env/jdbc/ExampleDatasource";
	public final String PROTOCOL = "jdbc:derby:"; 
	public final String OPEN = "Open";
	public final String SHUTDOWN = "Shutdown";
	public final String FILE_FORMAT = ".json";
	// string limitations
	public final int MAX_LENGTH_USERNAME = 10;
	public final int MAX_LENGTH_PASSWORD =  8;
	public final int MAX_LENGTH_NICKNAME = 20;
	public final int MAX_LENGTH_USER_DESCRIPTIONS = 50;
	public final int MAX_LENGTH_CHANNEL_NAME = 30;
	public final int MAX_LENGTH_CHANNEL_DESCRIPTIONS = 500;
	public final int MAX_LENGTH_MESSAGE = 500;
	// message properties
	public final String MESSAGE_PROPERTY = "messageType";
	public final String MESSAGE_CONTENT = "messageContent";
	// message types
	public final String MESSAGE_CREATE_CHANNEL = "createChannel";
	public final String MESSAGE_SUBSCRIBE = "subscribe";
	public final String MESSAGE_UNSUBSCRIBE = "unsubscribe";
	public final String MESSAGE_CHANNEL_DISCOVERY = "channelDiscovery";
	public final String MESSAGE_DOWNLOAD_MESSAGES = "downloadMessages";
	public final String MESSAGE_CHANNEL_VIEWING = "channelViewing";
	public final String MESSAGE_RECEIVED_MESSAGE = "sendMessage";
	public final String MESSAGE_UPDATE_COUNTERS = "updateCounters";
	// general constants
	public final int MESSAGES_TO_DOWNLOAD = 10;
	// users table
	public final String USERS = "users";
	public final String USERS_FILE = USERS + FILE_FORMAT;
	// newsql statements
	public final String CREATE_USER_TABLE = "CREATE TABLE USERS("
			+ "username varchar(10) NOT NULL,"
			+ "password varchar(32) NOT NULL,"
			+ "nickname varchar(20) NOT NULL,"
			+ "description varchar(50),"
			+ "avatarUrl varchar(500),"
			+ "PRIMARY KEY(username))";
	public final String INSERT_USER_STMT = "INSERT INTO " + USERS + " VALUES(?,?,?,?,?)";
	public final String SELECT_USER_BY_USERNAME_STMT = "SELECT * FROM " + USERS + " WHERE username=?";
	public final String SELECT_USER_BY_CREDENTIALS_STMT = "SELECT * FROM " + USERS + " WHERE username=? AND password=?";
	public final String SELECT_USERS = "SELECT * FROM " + USERS;
	// channels table
	public final String CHANNELS = "channels";
	public final String CHANNELS_FILE = CHANNELS + FILE_FORMAT;
	public final String CREATE_CHANNELS_TABLE = "CREATE TABLE " + CHANNELS + "("
			+ "channelId varchar(30) NOT NULL,"
			+ "description varchar(500),"
			+ "numberOfSubscribers integer,"
			+ "isPublic boolean,"
			+ "PRIMARY KEY(channelId))";
	public final String INSERT_CHANNEL_STMT = "INSERT INTO " + CHANNELS + " VALUES(?,?,?,?)";
	public final String SELECT_CHANNEL_BY_CHANNELNAME_STMT = "SELECT * FROM " + CHANNELS + " WHERE channelId=?";
	public final String SELECT_PUBLIC_CHANNEL_BY_CHANNELNAME_STMT = "SELECT * FROM " + CHANNELS + " WHERE isPublic=true AND channelId=?";
	public final String SELECT_CHANNELS = "SELECT * FROM " + CHANNELS;
	public final String UPDATE_CHANNEL_SUBSCRIBERS_COUNT_STMT = "UPDATE " + CHANNELS + " SET numberOfSubscribers=? WHERE channelId=?";
	// subscriptions table
	public final String SUBSCRIPTIONS = "subscriptions";
	public final String SUBSCRIPTIONS_FILE = SUBSCRIPTIONS + FILE_FORMAT;
	public final String CREATE_SUBSCRIPTION_TABLE = "CREATE TABLE " + SUBSCRIPTIONS + "("
			+ "channelId varchar(30) NOT NULL,"
			+ "userId varchar(10) NOT NULL,"
			+ "subscriptionTime timestamp,"
			+ "numberOfReadMessages integer,"
			+ "unreadMessages integer,"
			+ "unreadMentionedMessages integer,"
			+ "FOREIGN KEY(userId) REFERENCES " + USERS + "(username),"
			+ "FOREIGN KEY(channelId) REFERENCES " + CHANNELS + "(channelId))";
	public final String INSERT_SUBSCRIPTION_STMT = "INSERT INTO " + SUBSCRIPTIONS + " VALUES(?,?,?,?,?,?)";
	public final String SELECT_SUBSCRIPTIONS_BY_CHANNEL_STMT = "SELECT * FROM " + SUBSCRIPTIONS + " WHERE channelId=?";
	public final String SELECT_SUBSCRIPTIONS_BY_USERNAME_STMT = "SELECT * FROM " + SUBSCRIPTIONS + " WHERE userId=?";
	public final String SELECT_SUBSCRIPTIONS_BY_CHANNEL_AND_USER_STMT = "SELECT * FROM " + SUBSCRIPTIONS + " WHERE channelId=? AND userId=?";
	public final String SELECT_SUBSCRIPTIONS = "SELECT * FROM " + SUBSCRIPTIONS;
	public final String REMOVE_SUBSCRIPTION_BY_CHANNEL_AND_USER_STMT = "DELETE FROM " + SUBSCRIPTIONS + " WHERE channelId=? AND userId=?";
	public final String UPDATE_SUBSCRIPTION_STMT = "UPDATE " + SUBSCRIPTIONS + " SET numberOfReadMessages=?, unreadMessages=?, unreadMentionedMessages=? WHERE channelId=? AND userId=?";
	// messages table
	public final String MESSAGES = "messages";
	public final String MESSAGES_FILE = MESSAGES + FILE_FORMAT;
	public final String CREATE_MESSAGES_TABLE = "CREATE TABLE " + MESSAGES + "("
			+ "id integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
			+ "channelId varchar(30) NOT NULL,"
			+ "userId varchar(10) NOT NULL,"
			+ "messageTime timestamp,"
			+ "lastModified timestamp,"
			+ "repliedToId integer,"
			+ "content varchar(500),"
			+ "PRIMARY KEY(id),"
			+ "FOREIGN KEY(userId) REFERENCES " + USERS + "(username),"
			+ "FOREIGN KEY(channelId) REFERENCES " + CHANNELS + "(channelId))";
	public final String INSERT_MESSAGE_STMT = "INSERT INTO " + MESSAGES
			+ " (channelId, userId, messageTime, lastModified, repliedToId, content) VALUES(?,?,?,?,?,?)";
	public final String SELECT_MESSAGES_BY_CHANNEL_AND_REPLY_TO_ID_STMT = "SELECT * FROM " + MESSAGES + " WHERE channelId=? AND lastModified>=? AND repliedToId=? ORDER BY lastModified, messageTime, id";
	public final String SELECT_MESSAGE_BY_ID_STMT = "SELECT * FROM " + MESSAGES + " WHERE id=?";
	public final String SELECT_MESSAGE_BY_REPLY_TO_ID_STMT = "SELECT * FROM " + MESSAGES + " WHERE repliedToId=?";
	public final String SELECT_MESSAGES_STMT = "SELECT * FROM " + MESSAGES;
	public final String UPDATE_MESSAGE_LASTMODIFIED_STMT = "UPDATE " + MESSAGES + " SET lastModified=? WHERE id=?";
}
