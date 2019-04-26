import java.io.IOException;
import java.io.PrintWriter;
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
import com.google.gson.JsonArray;

@WebServlet("/StarPage")
public class StarPage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public StarPage() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String star_name = request.getParameter("s_name");
	
		response.setContentType("application/html");
		PrintWriter out = response.getWriter();
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/MovieDB";
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement statement = dbcon.createStatement();
			Statement movieStatement = dbcon.createStatement();
			String query = "SELECT DISTINCT S.name, S.birthYear "
							+ "FROM movies M, stars S, stars_in_movies SIM " 
							+ "WHERE S.name = \"" + star_name + "\" AND SIM.starId = S.id "
							+ "AND SIM.movieId = M.id";
			
			ResultSet rs = statement.executeQuery(query);
			JsonArray jsonArray = new JsonArray();
			
			while (rs.next()) {
				String s_name = rs.getString("S.name");
				String s_bYear = rs.getString("S.birthYear");
				String movieList = createQueryList(movieStatement, s_name);
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("star_name", s_name);
				jsonObject.addProperty("dob", s_bYear);
				jsonObject.addProperty("movie_list", movieList);
				
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public String createMovieQuery(String starName) {
		return "SELECT M.id, M.title " +
		       "FROM movies M, stars_in_movies SIM ,stars S " +
		       "WHERE S.name = \"" + starName + "\" AND S.id = SIM.starId AND M.id = SIM.movieId";
	}
	
	public String createQueryList(Statement queryStatement, String starName) {
		String myQuery = "";
		String selectList = "";
		myQuery += createMovieQuery(starName);
		try {
			ResultSet aRS = queryStatement.executeQuery(myQuery);
			selectList += resultSetToString(aRS);
			aRS.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return selectList;
	}
	
	
	public String resultSetToString(ResultSet rSet) {
		String resultList = "";
		try {
			while (rSet.next()) {
				resultList += rSet.getString("M.id");
				resultList += ":";
				resultList += rSet.getString("M.title");
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
}