import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class Login
 */
@WebServlet("/CustomerInfo")
public class CustomerInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CustomerInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String first_name = request.getParameter("first_name");
		String last_name = request.getParameter("last_name");
		String cc_num = request.getParameter("cc_num");
		String expire_date = request.getParameter("expire_date");
		if (expire_date.charAt(4) == '/') {
			String ExpDateArray[] = expire_date.split("/");
			String tempExpDate = "";
			for(String temp: ExpDateArray) {
				tempExpDate += temp;
			}
			expire_date = tempExpDate;
		}
		
		// this example only allows username/password to be test/test
		// in the real project, you should talk to the database to verify username/password
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/MovieDB";
		
		response.setContentType("text/html");
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement statement = dbcon.createStatement();
			String query = "SELECT * FROM creditcards " + 
					   "WHERE firstName = \"" + first_name + "\" AND lastName = \"" + last_name + "\" " +
					   "AND id = \"" + cc_num + "\" AND expiration = \"" + expire_date + "\"";
			ResultSet rs = statement.executeQuery(query);
			rs.first();
			String firstName = rs.getString("firstName");
			String lastName = rs.getString("lastName");
			String ccNum = rs.getString("id");
			String expireDate = rs.getString("expiration");
			
			if (first_name.equals(firstName) && last_name.equals(lastName) && 
				cc_num.equals(ccNum) && expire_date.equals(expireDate)) {
				// login success:
				// set this user into the session
				//request.getSession().setAttribute("user", new User(firstName));
				
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("message", "success");
				
				response.getWriter().write(responseJsonObject.toString());
			} else {
				// login fail
				//request.getSession().setAttribute("user", new User(firstName));
				
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "fail");
				if (! (first_name.equals(firstName))) {
					responseJsonObject.addProperty("message", "first name " + first_name + " doesn't exist");
				} else if (! (last_name.equals(lastName))) {
					responseJsonObject.addProperty("message", "last name " + last_name + " doesn't exist");
				} if (! (cc_num.equals(ccNum))) {
					responseJsonObject.addProperty("message", "credit card num " + cc_num + " doesn't exist");
				}
				if (! (expire_date.equals(expireDate))) {
					responseJsonObject.addProperty("message", "expire date " + expire_date + " doesn't exist");
				}
				
				response.getWriter().write(responseJsonObject.toString());
			}
			
			rs.close();
			statement.close();
			dbcon.close();

			
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		
		catch (java.lang.Exception ex) {
			response.getWriter().println("<HTML>" + "<HEAD><TITLE>" + "UCIMDb: Error on Login Page" + "</TITLE></HEAD>\n<BODY>"
	                + "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
			return;
		}	
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
