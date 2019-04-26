import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MovieList
 */
@WebServlet("/AddNewStar")
public class AddNewStar extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public String getServletInfo() {
		return "Servlet connects to MySQL database and displays result of a SELECT";
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddNewStar() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		String star_name = request.getParameter("starName");
		String star_year = request.getParameter("birthYear");
		
//		String loginUser = "mytestuser";
//		String loginPasswd = "mypassword";
//		String loginUrl = "jdbc:mysql://localhost:3306/MovieDB";
		
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		try {
			//Class.forName("com.mysql.jdbc.Driver").newInstance();			
			//Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			
			//CONNECTION POOLING
			Context initCtx = new InitialContext(); 
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            DataSource ds = (DataSource) envCtx.lookup("jdbc/MasterDB");
            
            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");
			
			if(star_year == "") {
				star_year = null;
			}
		
			Statement IDStatement = dbcon.createStatement();
			String id = getNewID(IDStatement);
			String query = "INSERT INTO stars (id, name, birthYear)" +
						   " VALUES(?, ?, ?)"; 
			PreparedStatement insertStatement = dbcon.prepareStatement(query);
			insertStatement.setString(1, id);
			insertStatement.setString(2, star_name);
			insertStatement.setString(3, star_year);
			insertStatement.executeUpdate();
			
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("status", "success");
			responseJsonObject.addProperty("message", "success");

			out.write(responseJsonObject.toString());
			insertStatement.close();
			dbcon.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		
		catch (java.lang.Exception ex) {
			out.println("<HTML>" + "<HEAD><TITLE>" + "UCIMDb: Error" + "</TITLE></HEAD>\n<BODY>"
		                + "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
			return;
		}
		out.close();
		
	}
	
	public String getNewID(Statement queryStatement) {
		String newID = "";
		String stringID = "";
		String IDquery = "SELECT max(id) FROM stars";
		int num;
		try {
			ResultSet rs = queryStatement.executeQuery(IDquery);
			rs.first();
			stringID = rs.getString("max(id)");
			//stringID += resultSetToString(rs, "id");
			String numID = stringID.substring(2);
			num = Integer.parseInt(numID);
			num+=1;
			newID = stringID.substring(0,2) + Integer.toString(num);
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return newID;
	}
	
	public String resultSetToString(ResultSet rSet, String selectName) {
		String resultList = "";
		try {
			while (rSet.next()) {
				resultList += rSet.getString(selectName);
				if (!rSet.isLast())
					resultList += ", ";
			}
		} catch (SQLException ex) {
			while (ex != null ) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return resultList;
	}

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
