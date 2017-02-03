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
 * Servlet implementation class CustomersServlet1
 */
@WebServlet(
		description = "Servlet to provide details about customers", 
		urlPatterns = { 
				"/login"
		})
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
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
			response.setContentType("application/json");

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

			// parse the data
			Gson gson = new Gson();
			UserCredentials credentials = gson.fromJson(gsonData, UserCredentials.class);
						
			User user = null;
			if ((user = DataManager.getUserByCredentials(conn, credentials.getUsername(), credentials.getPassword())) != null) { // user exists
				// prepare response to client
				AuthSuccess authSucces = BuildSuccessMessages.buildAuthSuccess(conn, credentials, ThreadUser.getThreadUserByUser(user));
				if (authSucces != null) {
					writer.write(gson.toJson(authSucces));
				} else {
					writer.write(gson.toJson(new AuthFailure("General error")));
				}
			} else { // user doesn't exist
				if (DataManager.getUserByUsername(conn, credentials.getUsername()) == null) { // user with this name does not exist
					writer.write(gson.toJson(new AuthFailure("Username does not exist")));
				} else { // user with this name exists, but wrong password
					writer.write(gson.toJson(new AuthFailure("Incorrect Password")));
				}
			}
			writer.close();
			conn.close();
		} catch (SQLException | NamingException e ) {
			getServletContext().log("Error while closing connection", e);
			response.sendError(500);//internal server error
		}
	}
}
