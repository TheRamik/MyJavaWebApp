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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MovieList
 */
@WebServlet("/EmployeeDashboard")
public class EmployeeDashboard extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public String getServletInfo() {
		return "Servlet connects to MySQL database and displays result of a SELECT";
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeeDashboard() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
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

            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
            
            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");
			
			Statement statement = dbcon.createStatement();
			Statement tableAttributeStatement = dbcon.createStatement();
			String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'MovieDB'"; 
			ResultSet rs = statement.executeQuery(query);
			
			JsonArray jsonArray = new JsonArray();
			
			while (rs.next()) {
				String category = rs.getString("table_name");
				String attributeList = createQueryList(tableAttributeStatement, category, "COLUMN_NAME");
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("table_category", category);
				jsonObject.addProperty("attribute_list", attributeList);
				
				jsonArray.add(jsonObject);
			}
			
			out.write(jsonArray.toString());

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
			out.println("<HTML>" + "<HEAD><TITLE>" + "UCIMDb: Error" + "</TITLE></HEAD>\n<BODY>"
		                + "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
			return;
		}
		out.close();
		
	}
	
	public String createAttributeQuery(String tableName) {
		return "SELECT COLUMN_NAME FROM information_schema.columns WHERE table_schema='MovieDB' " +
			   "AND table_name='" + tableName + "'";
	}
	
	public String createQueryList(Statement queryStatement, String movieTitle, String selectName) {
		String myQuery = "";
		String selectList = "";
		
		myQuery += createAttributeQuery(movieTitle);
		try {
			ResultSet aRS = queryStatement.executeQuery(myQuery);
			selectList += resultSetToString(aRS, selectName);
			aRS.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return selectList;
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
