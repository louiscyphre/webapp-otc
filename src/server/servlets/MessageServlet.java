package server.servlets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.google.gson.Gson;

import server.AppConstants;
import server.DataManager;
import server.messages.MessageReceived;
import server.model.Message;
import server.model.MessageCredentials;
import server.model.ThreadUser;
import server.model.User;

/**
 * Servlet implementation class CustomersServlet1
 */
@WebServlet(
		description = "Servlet to provide details about customers", 
		urlPatterns = { 
				"/message"
		})
public class MessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MessageServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	try {
    		response.sendError(401);
    	} catch (Exception  e) {
    		getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
    	}

    	
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// set encoding to UTF-8
			response.setCharacterEncoding("UTF-8");
		    response.setContentType("text/xml");
        	
			//obtain CustomerDB data source from Tomcat's context
			Context context = new InitialContext();
			BasicDataSource ds = (BasicDataSource)context.lookup(getServletContext().getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.OPEN);
			Connection conn = ds.getConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		    PrintWriter writer = response.getWriter();
			
			// read the data sent from the client
			String gsonData = "";
			if (br != null) {
				gsonData = br.readLine();
			}
System.out.println("read me pls: " + gsonData);
			// parse the data
			Gson gson = new Gson();
			MessageCredentials credentials = gson.fromJson(gsonData, MessageCredentials.class);
			
			if (DataManager.getChannelByName(conn, credentials.getChannel()) != null) { // check if channel exists
				User user = null;
				if ((user = DataManager.getUserByUsername(conn, credentials.getUsername())) != null) { // check if user exists
					if (DataManager.getSubscriptionByChannelAndUsername(conn, credentials.getChannel(), credentials.getUsername()) != null) { // check if subscribed
						Message message = DataManager.addMessage(conn, credentials, new ThreadUser(user.getUsername(), user.getNickname(), user.getDescription(), user.getAvatarUrl()));
						writer.write(gson.toJson(new MessageReceived()));
					} else {
						writer.write(gson.toJson(new MessageReceived("Not subscribed to channel")));
					}
				} else {
					writer.write(gson.toJson(new MessageReceived("User does not exist")));
				}
			} else { // channel doesn't exist
				writer.write(gson.toJson(new MessageReceived("Channel does not exist")));
			}
        	writer.close();
			conn.close();
		} catch (SQLException | NamingException e ) {
			getServletContext().log("Error while closing connection", e);
			response.sendError(500);//internal server error
		}
	}
}
