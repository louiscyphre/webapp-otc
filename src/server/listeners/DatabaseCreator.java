package server.listeners;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import server.AppConstants;
import server.model.User;



/**
 * An server listener that reads the customer json file and populates the data into a Derby database
 */
@WebListener
public class DatabaseCreator implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public DatabaseCreator() {
    }
    
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
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event)  { 
    	ServletContext cntx = event.getServletContext();
    	
    	try {
    		//obtain CustomerDB data source from Tomcat's context
    		Context context = new InitialContext();
    		BasicDataSource ds = (BasicDataSource)context.lookup(
    				cntx.getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.OPEN);
    		Connection conn = ds.getConnection();
    		
    		//Connection conn1 = ds.getConnection();
			//Statement stmt1 = conn1.createStatement();
			//stmt1.executeUpdate("DROP TABLE USERS");
    		//conn1.commit();
			//stmt1.executeUpdate("DROP TABLE USER");
    		//conn1.commit();
			//stmt1.executeUpdate("DROP TABLE CUSTOMER");
    		//conn1.commit();
    		//stmt1.close();
    		
    		boolean created = false;
    		try{

    			Statement stmt = conn.createStatement();
    			stmt.executeUpdate(AppConstants.CREATE_USER_TABLE);

        		conn.commit();
        		stmt.close();
        		
    		} catch (SQLException e){
    			created = tableAlreadyExists(e);
    			if (!created){
    				throw e;
    			}
    		}
    		
    		//if no database exist in the past - further populate its records in the table
    		if (!created){
                // Create users table with user data from json file
                Collection<User> users = loadUsers(cntx.getResourceAsStream(File.separator +
                        AppConstants.USERS_FILE));
                PreparedStatement pstmt2 = conn.prepareStatement(AppConstants.INSERT_USER_STMT);
                for (User user : users) {
                	
                	System.out.println(user.stringify());
                	
                    pstmt2.setString(1, user.getIdHash());
                    pstmt2.setString(2, user.getUsername());
                    pstmt2.setString(3, user.getPasswordHash());
                    pstmt2.setString(4, user.getNickname());
                    pstmt2.setString(5, user.getDescription());
                    pstmt2.setString(6, user.getAvatarUrl());
                    pstmt2.executeUpdate();
                }
                conn.commit();
                pstmt2.close();
    		}
    		//close connection
    		conn.close();

    	} catch (IOException | SQLException | NamingException e) {
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
     		BasicDataSource ds = (BasicDataSource)context.lookup(
     				cntx.getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.SHUTDOWN);
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
}
