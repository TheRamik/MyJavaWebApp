import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class Login
 */
@WebServlet("/EmployeeLogin")
public class EmployeeLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeeLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
		// Verify CAPTCHA.
		boolean valid = VerifyUtils.verify(gRecaptchaResponse);
		if (!valid) {
		    //errorString = "Captcha invalid!";
		    out.println("<HTML>" +
				"<HEAD><TITLE>" +
				"MovieDB: Error" +
				"</TITLE></HEAD>\n<BODY>" +
				"<P>U NOT HOOMAN :0</P></BODY></HTML>");
		    return;
		}
//		out.println("<HTML>" +
//				"<HEAD><TITLE>" +
//				"MovieDB: SUCCESS" +
//				"</TITLE></HEAD>\n<BODY>" +
//				"<P>" + valid + "</P></BODY></HTML>");
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		
		// this example only allows username/password to be test/test
		// in the real project, you should talk to the database to verify username/password
//		String loginUser = "mytestuser";
//		String loginPasswd = "mypassword";
//		String loginUrl = "jdbc:mysql://localhost:3306/MovieDB";
		
		response.setContentType("text/html");
		
		try {
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
			//Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			
			//CONNECTION POOLING
			Context initCtx = new InitialContext(); 
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
            
            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");
			
			Statement statement = dbcon.createStatement();
			String query = "SELECT * FROM employees " + 
					   "WHERE email = \"" + username + "\" AND password = \"" + password + "\"";
			ResultSet rs = statement.executeQuery(query);
			rs.first();
			String c_username = rs.getString("email");
			String c_password = rs.getString("password");
		
			if (username.equals(c_username) && password.equals(c_password)) {
				// login success:
				// set this user into the session
				request.getSession().setAttribute("user", new User(username));
				
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("message", "success");
				
				response.getWriter().write(responseJsonObject.toString());
			} else {
				// login fail
				request.getSession().setAttribute("user", new User(username));
				
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "fail");
				if (! (username.equals(c_username))) {
					responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
				} else if (! (password.equals(c_password))) {
					responseJsonObject.addProperty("message", "incorrect password");
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
