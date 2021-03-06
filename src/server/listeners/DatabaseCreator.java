/*
 *     webapp-otc - an online collaboration tool .
 *     Copyright (C) 2017 Ilia Butvinnik and Michael Goldman
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package server.listeners;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import server.AppConstants;
import server.model.Channel;
import server.model.MessageDB;
import server.model.Subscription;
import server.model.User;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;



/**
 * An server listener that reads the users, channels, subscriptions and messages json file(s) and populates the data into a Derby database
 */
@WebListener
public class DatabaseCreator implements ServletContextListener {

    /**
     * Default C'tor
     */
	public DatabaseCreator() {}
    
	/**
	 * Checks if a table already exists in the database
	 * @param e an SQL exception that will be checked if it's an exception of a table that is already exists
	 * @return true if the table already exists and false otherwise.
	 */
    private boolean tableAlreadyExists(SQLException e) {
        boolean exists;
        if(e.getSQLState().equals("X0Y32")) {
            exists = true;
        } else {
            exists = false;
        }
        return exists;
    }


	/**
	 * initializes the database
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event)  { 
    	ServletContext cntx = event.getServletContext();
    	
    	try {
    		//obtain CustomerDB data source from Tomcat's context
    		Context context = new InitialContext();
    		BasicDataSource ds = (BasicDataSource)context.lookup(cntx.getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.OPEN);
    		Connection conn = ds.getConnection();
    		
    		/*
    		// to start the project with an empty database, uncomment this part
			try {
    			Statement stmt = conn.createStatement();
    			stmt.execute("DROP TABLE " + AppConstants.MESSAGES);
    			stmt.execute("DROP TABLE " + AppConstants.SUBSCRIPTIONS);
    			stmt.execute("DROP TABLE " + AppConstants.CHANNELS);
    			stmt.execute("DROP TABLE " + AppConstants.USERS);
    			conn.commit();
    			stmt.close();
			} catch (SQLException e) { }
    		*/
    		
    		boolean created = false; // will tell if a table already exists in the database
    		try{
    			Statement stmt = conn.createStatement();
    			stmt.executeUpdate(AppConstants.CREATE_USER_TABLE);
        		conn.commit();
        		stmt.close();
    		} catch (SQLException e){
    			if (!(created = tableAlreadyExists(e))){ // if not a 'table already exists' exception, rethrow 
    				throw e;
    			}
    		}
    		
    		//if no database exist in the past - further populate its records in the table
    		if (!created){
                // Create users table with user data from json file
    			try {
	                Collection<User> users = loadUsers(cntx.getResourceAsStream(File.separator + AppConstants.USERS_FILE));
	                PreparedStatement pstmt2 = conn.prepareStatement(AppConstants.INSERT_USER_STMT);
	                for (User user : users) {
	                    pstmt2.setString(1, user.getUsername());
	                    pstmt2.setString(2, user.getPassword());
	                    pstmt2.setString(3, user.getNickname());
	                    pstmt2.setString(4, user.getDescription());
	                    pstmt2.setString(5, user.getAvatarUrl());
	                    pstmt2.executeUpdate();
	                }
	                conn.commit();
	                pstmt2.close();
    			} catch (IOException | NullPointerException e) { }
    		}
    		
    		created = false;
    		try{
    			Statement stmt = conn.createStatement();
    			stmt.executeUpdate(AppConstants.CREATE_CHANNELS_TABLE);
        		conn.commit();
        		stmt.close();
    		} catch (SQLException e){
    			if (!(created = tableAlreadyExists(e))){ // if not a 'table already exists' exception, rethrow
    				throw e;
    			}
    		}
    		
    		//if no database exist in the past - further populate its records in the table
    		if (!created){
                // Create channels table with channel data from json file
    			try {
	                Collection<Channel> channels = loadChannels(cntx.getResourceAsStream(File.separator + AppConstants.CHANNELS_FILE));
	                PreparedStatement pstmt2 = conn.prepareStatement(AppConstants.INSERT_CHANNEL_STMT);
	                for (Channel channel : channels) {
	                    pstmt2.setString (1, channel.getChannelName());
	                    pstmt2.setString (2, channel.getDescription());
	                    pstmt2.setInt    (3, channel.getNumberOfSubscribers());
	                    pstmt2.setBoolean(4, channel.isPublic());
	                    pstmt2.executeUpdate();
	                }
	                conn.commit();
	                pstmt2.close();
    			} catch (IOException | NullPointerException e) { }
    		}
    		
    		created = false;
    		try{
    			Statement stmt = conn.createStatement();
    			stmt.executeUpdate(AppConstants.CREATE_SUBSCRIPTION_TABLE);
        		conn.commit();
        		stmt.close();
    		} catch (SQLException e){
    			if (!(created = tableAlreadyExists(e))) { // if not a 'table already exists' exception, rethrow
    				throw e;
    			}
    		}
    		
    		//if no database exist in the past - further populate its records in the table
    		if (!created){
                // Create subscriptions table with subscription data from json file
    			try {
	                Collection<Subscription> subscriptions = loadSubscriptions(cntx.getResourceAsStream(File.separator + AppConstants.SUBSCRIPTIONS_FILE));
	                PreparedStatement pstmt2 = conn.prepareStatement(AppConstants.INSERT_SUBSCRIPTION_STMT);
	                for (Subscription subscription : subscriptions) {
	                    pstmt2.setString (1, subscription.getChannelName());
	                    pstmt2.setString (2, subscription.getUsername());
	                    pstmt2.setTimestamp(3, subscription.getSubscriptionTime());
	                    pstmt2.setInt(4, subscription.getNumberOfReadMessages());
	                    pstmt2.setInt(5, subscription.getUnreadMessages());
	                    pstmt2.setInt(6, subscription.getUnreadMentionedMessages());
	                    pstmt2.executeUpdate();
	                }
	                conn.commit();
	                pstmt2.close();
    			} catch (IOException | NullPointerException e) { }
    		}
    		
    		created = false;
    		try{
    			Statement stmt = conn.createStatement();
    			stmt.executeUpdate(AppConstants.CREATE_MESSAGES_TABLE);
        		conn.commit();
        		stmt.close();
    		} catch (SQLException e){
    			if (!(created = tableAlreadyExists(e))){ // if not a 'table already exists' exception, rethrow
    				throw e;
    			}
    		}

    		//if no database exist in the past - further populate its records in the table
    		if (!created){
                // Create messages table with message data from json file
    			try {
	                Collection<MessageDB> messages = loadMessages(cntx.getResourceAsStream(File.separator + AppConstants.MESSAGES_FILE));
	                PreparedStatement pstmt2 = conn.prepareStatement(AppConstants.INSERT_MESSAGE_STMT);
	                for (MessageDB message : messages) {
	                    pstmt2.setString   (1, message.getChannelId());
	                    pstmt2.setString   (2, message.getUserId());
	                    pstmt2.setTimestamp(3, message.getMessageTime());
	                    pstmt2.setTimestamp(4, message.getLastModified());
	                    pstmt2.setInt      (5, message.getRepliedToId());
	                    pstmt2.setString   (6, message.getContent());
	                    pstmt2.executeUpdate();
	                }
	                conn.commit();
	                pstmt2.close();
    			} catch (IOException | NullPointerException e) { }
    		}
    		
    		//close connection
    		conn.close();

    	} catch (SQLException | NamingException e) {
    		//log error 
    		cntx.log("Error during database initialization",e);
    	}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event)  { 
    	 ServletContext cntx = event.getServletContext();
    	 
         //shut down database
    	 try {
     		Context context = new InitialContext();
     		BasicDataSource ds = (BasicDataSource)context.lookup(cntx.getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.SHUTDOWN);
     		ds.getConnection();
     		ds = null;
		} catch (SQLException | NamingException e) {
			cntx.log("Error shutting down database",e);
		}
    }
 
    /**
     * Loads Users data from json file that is read from the input stream into
     * a collection of User objects
     *
     * @param is input stream to json file
     * @return collection of Users
     * @throws IOException
     */
    private Collection<User> loadUsers(InputStream is) throws IOException {
    	try {
    		if (is != null) {
    			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    			StringBuilder jsonFileContent = new StringBuilder();

    			String nextLine = null;
    			while ((nextLine = reader.readLine()) != null) {
    				jsonFileContent.append(nextLine);
    			}

    			Gson gson = new Gson();
    			Type type = new TypeToken<Collection<User>>() { }.getType();
    			Collection<User> users = gson.fromJson(jsonFileContent.toString(), type);
    			reader.close();
    			return users;
    		}
    		return null;
		} catch (NullPointerException e) {
			return null;
		}
    }
 
    /**
     * Loads Channels data from json file that is read from the input stream into
     * a collection of Channel objects
     *
     * @param is input stream to json file
     * @return collection of Channels
     * @throws IOException
     */
    private Collection<Channel> loadChannels(InputStream is) throws IOException {
    	try {
    		if (is != null) {
    			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    			StringBuilder jsonFileContent = new StringBuilder();

    			String nextLine = null;
    			while ((nextLine = reader.readLine()) != null) {
    				jsonFileContent.append(nextLine);
    			}

    			Gson gson = new Gson();
    			Type type = new TypeToken<Collection<Channel>>() { }.getType();
    			Collection<Channel> channels = gson.fromJson(jsonFileContent.toString(), type);
    			reader.close();
    			return channels;
    		}
    		return null;
    	} catch (NullPointerException e) {
    		return null;
    	}
    }
 
    /**
     * Loads Subscriptions data from json file that is read from the input stream into
     * a collection of Subscription objects
     *
     * @param is input stream to json file
     * @return collection of Subscriptions
     * @throws IOException
     */
    private Collection<Subscription> loadSubscriptions(InputStream is) throws IOException {

    	try {
    		if (is != null) {
		        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		        StringBuilder jsonFileContent = new StringBuilder();
		
		        String nextLine = null;
		        while ((nextLine = reader.readLine()) != null) {
		            jsonFileContent.append(nextLine);
		        }
		
		        Gson gson = new Gson();
		        Type type = new TypeToken<Collection<Subscription>>() { }.getType();
		        Collection<Subscription> subscriptions = gson.fromJson(jsonFileContent.toString(), type);
		        reader.close();
		        return subscriptions;
    		}
    		return null;
    	} catch (NullPointerException e) {
    		return null;
    	}
    }
 
    /**
     * Loads Messages data from json file that is read from the input stream into
     * a collection of Message objects
     *
     * @param is input stream to json file
     * @return collection of Messages
     * @throws IOException
     */
    private Collection<MessageDB> loadMessages(InputStream is) throws IOException {
    	try {
    		if (is != null) {
	    		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    		StringBuilder jsonFileContent = new StringBuilder();
	
	    		String nextLine = null;
	    		while ((nextLine = reader.readLine()) != null) {
	    			jsonFileContent.append(nextLine);
	    		}
	
	    		Gson gson = new Gson();
	    		Type type = new TypeToken<Collection<MessageDB>>() { }.getType();
	    		Collection<MessageDB> messages = gson.fromJson(jsonFileContent.toString(), type);
	    		reader.close();
	    		return messages;
    		}
    		return null;
    	} catch (NullPointerException e) {
    		return null;
    	}
    }
}
