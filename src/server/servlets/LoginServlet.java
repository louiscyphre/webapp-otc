package server.servlets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.security.NoSuchAlgorithmException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.google.gson.Gson;

import server.AppConstants;
import server.model.Customer;
import server.model.UserCredentials;
import server.util.Hash;


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
    		
        	//obtain CustomerDB data source from Tomcat's context
    		Context context = new InitialContext();
    		BasicDataSource ds = (BasicDataSource)context.lookup(
    				getServletContext().getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.OPEN);
    		Connection conn = ds.getConnection();

    		Collection<Customer> customersResult = new ArrayList<Customer>(); 
    		String uri = request.getRequestURI();
    		if (uri.indexOf(AppConstants.NAME) != -1){//filter customer by specific name
    			String name = uri.substring(uri.indexOf(AppConstants.NAME) + AppConstants.NAME.length() + 1);
    			PreparedStatement stmt;
    			try {
    				stmt = conn.prepareStatement(AppConstants.SELECT_CUSTOMER_BY_NAME_STMT);
    				name = name.replaceAll("\\%20", " ");
    				stmt.setString(1, name);
    				ResultSet rs = stmt.executeQuery();
    				while (rs.next()){
    					customersResult.add(new Customer(rs.getString(1),rs.getString(2),rs.getString(3)));
    				}
    				rs.close();
    				stmt.close();
    			} catch (SQLException e) {
    				getServletContext().log("Error while querying for customers", e);
    	    		response.sendError(500);//internal server error
    			}
    		}else{
    			Statement stmt;
    			try {
    				stmt = conn.createStatement();
    				ResultSet rs = stmt.executeQuery(AppConstants.SELECT_ALL_CUSTOMERS_STMT);
    				while (rs.next()){
    					customersResult.add(new Customer(rs.getString(1),rs.getString(2),rs.getString(3)));
    				}
    				rs.close();
    				stmt.close();
    			} catch (SQLException e) {
    				getServletContext().log("Error while querying for customers", e);
    	    		response.sendError(500);//internal server error
    			}

    		}

    		conn.close();
    		
    		Gson gson = new Gson();
        	//convert from customers collection to json
        	String customerJsonResult = gson.toJson(customersResult, AppConstants.CUSTOMER_COLLECTION);

        	PrintWriter writer = response.getWriter();
        	writer.println(customerJsonResult);
        	writer.close();
    	} catch (SQLException | NamingException e) {
    		getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
    	}

    	
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {

			//obtain CustomerDB data source from Tomcat's context
			Context context = new InitialContext();
			BasicDataSource ds = (BasicDataSource)context.lookup(getServletContext().getInitParameter(AppConstants.DB_DATASOURCE) + AppConstants.OPEN);
			Connection conn = ds.getConnection();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			
			String gsonData = "";
			if (br != null) {
				gsonData = br.readLine();
			}
            System.out.println("!!!!!!!!!!!!!!!!!!"+gsonData+"!!!!!!!!!!!!!!!!!!!!!");
            Gson gson = new Gson();
			UserCredentials credentials = gson.fromJson(gsonData, UserCredentials.class);
            System.out.println("!!!!!!!!!!!!!!!!!!"+credentials.getUsername()+ " " + credentials.getPassword() + "!!!!!!!!!!!!!!!!!!!!!");
			
			PreparedStatement stmt;
			try {
				stmt = conn.prepareStatement(AppConstants.SELECT_USER_BY_CREDENTIALS_STMT);
				stmt.setString(1, credentials.getUsername());
				//stmt.setString(2, Hash.getSha256Hex(credentials.getPassword()));
				stmt.setString(2, credentials.getPassword());
				
				ResultSet resultSet = stmt.executeQuery();
	        	PrintWriter writer = response.getWriter();
	        	
				if (resultSet.next()) {
					//HttpSession session = request.getSession(true);
					String userid = resultSet.getString(1);
					
					// FIXME
		        	writer.println(userid + " OK");

				} else {
		            System.out.println("!!!!!!!!!!!!!!!!!!NO LOGIIN!!!!!!!!!!!!!!!!!!!!!");
					writer.println("incorrect login");
					response.sendError(401);
				}
				resultSet.close();
	        	writer.close();
				stmt.close();
			} catch (SQLException e) {
				getServletContext().log("Error while querying for customers", e);
				response.sendError(500);//internal server error
			}

			conn.close();
		//} catch (SQLException | NamingException | NoSuchAlgorithmException e ) {	
		} catch (SQLException | NamingException e ) {
			getServletContext().log("Error while closing connection", e);
			response.sendError(500);//internal server error
		}
	}
}
