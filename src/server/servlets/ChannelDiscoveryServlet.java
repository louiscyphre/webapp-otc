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
import server.messages.Discovery;
import server.model.ChannelDiscovery;

/**
 * Servlet implementation class CustomersServlet1
 */
@WebServlet(
		description = "Servlet to provide details about customers", 
		urlPatterns = { 
				"/discovery"
		})
public class ChannelDiscoveryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ChannelDiscoveryServlet() {
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
			
			// parse the data
			Gson gson = new Gson();
			ChannelDiscovery credentials = gson.fromJson(gsonData, ChannelDiscovery.class);
			writer.write(gson.toJson(new Discovery(DataManager.discoverChannels(conn, credentials))));
			writer.close();
			conn.close();
		} catch (SQLException | NamingException e ) {
			getServletContext().log("Error while closing connection", e);
			response.sendError(500);//internal server error
		}
	}
}
