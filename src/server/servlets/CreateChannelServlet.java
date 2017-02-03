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
import server.messages.ChannelFailure;
import server.messages.ChannelSuccess;
import server.model.ChannelCredentials;
import server.model.Subscription;

/**
 * Servlet implementation class CustomersServlet1
 */
@WebServlet(
		description = "Servlet to provide details about customers", 
		urlPatterns = { 
				"/createChannel"
		})
public class CreateChannelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateChannelServlet() {
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
System.out.println("i'm a channel, create me pls: " + gsonData);
			// parse the data
			Gson gson = new Gson();
			ChannelCredentials credentials = gson.fromJson(gsonData, ChannelCredentials.class);
			
			// query the database and prepare the response
			if (DataManager.getChannelByName(conn, credentials.getName()) == null) { // channel does not exist
				DataManager.addChannel(conn, credentials);
				DataManager.addSubscription(conn, new Subscription(credentials.getName(), credentials.getOwner()));
				if (credentials.getUsername() != null)
					DataManager.addSubscription(conn, new Subscription(credentials.getName(), credentials.getUsername()));
				writer.write(gson.toJson(new ChannelSuccess()));
			} else { // channel exists
				writer.write(gson.toJson(new ChannelFailure("Channel already exists")));
			}
			conn.close();
        	writer.close();
		} catch (SQLException | NamingException e ) {
			getServletContext().log("Error while closing connection", e);
			response.sendError(500);//internal server error
		}
	}
}
