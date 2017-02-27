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
import server.messages.AuthFailure;
import server.messages.AuthSuccess;
import server.model.ThreadUser;
import server.model.User;
import server.model.UserCredentials;
import server.util.BuildSuccessMessages;


/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet(
		description = "Servlet to register users into the chat", 
		urlPatterns = { 
				"/register"
		})
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
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
        	
			//obtain projectDB data source from Tomcat's context
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

			// parse the data
			Gson gson = new Gson();
			User newUser = gson.fromJson(gsonData, User.class);
			System.out.println("newuser: " + newUser.toString());
			if (newUser.getUsername() == null || newUser.getUsername().isEmpty() || newUser.getUsername().trim().isEmpty()) {
				writer.write(gson.toJson(new AuthFailure("Username must not be empty")));
				return;
			} else if (newUser.getUsername().length() > AppConstants.MAX_LENGTH_USERNAME) {
				writer.write(gson.toJson(new AuthFailure("Username too long")));
				return;
			} else if (newUser.getPassword() == null || newUser.getPassword().isEmpty() || newUser.getPassword().trim().isEmpty()) {
				writer.write(gson.toJson(new AuthFailure("Password must not be empty")));
				return;
			} else if (newUser.getPassword().length() > AppConstants.MAX_LENGTH_PASSWORD) {
				writer.write(gson.toJson(new AuthFailure("Password too long")));
				return;
			} else if (newUser.getNickname() != null && newUser.getNickname().length() > AppConstants.MAX_LENGTH_PASSWORD) {
				writer.write(gson.toJson(new AuthFailure("Nickname too long")));
				return;
			} else if (newUser.getDescription() != null && newUser.getDescription().length() > AppConstants.MAX_LENGTH_PASSWORD) {
				writer.write(gson.toJson(new AuthFailure("Description too long")));
				return;
			}
			
			if (newUser.getNickname() == null || newUser.getNickname().isEmpty()) {
				newUser.setNickname(newUser.getUsername());
			}
			if (newUser.getAvatarUrl() == null || newUser.getAvatarUrl().isEmpty()) {
				newUser.setAvatarUrl(AppConstants.DEFAULT_AVATAR);
			}
			ThreadUser thUser = new ThreadUser(newUser.getUsername(), newUser.getNickname(), newUser.getDescription(), newUser.getAvatarUrl());
			UserCredentials credentials = new UserCredentials(newUser.getUsername(), newUser.getPassword());
			
			if (DataManager.getUserByUsername(conn, newUser.getUsername()) == null) { // user does not exist
				DataManager.addUser(conn, newUser); // create a new user
				// prepare response to client
				AuthSuccess authSuccess = BuildSuccessMessages.buildAuthSuccess(conn, credentials, thUser);
				if (authSuccess != null) {
					writer.write(gson.toJson(authSuccess));
				} else {
					writer.write(gson.toJson(new AuthFailure("General error")));
				}
			} else { // user exists
				writer.write(gson.toJson(new AuthFailure("Username already exists")));
			}
        	writer.close();
			conn.close();
		} catch (SQLException | NamingException e) {
			getServletContext().log("Error while closing connection", e);
			response.sendError(500);//internal server error
		}
	}
}
