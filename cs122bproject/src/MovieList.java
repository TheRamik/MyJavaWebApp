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
@WebServlet("/MovieList")
public class MovieList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public String getServletInfo() {
		return "Servlet connects to MySQL database and displays result of a SELECT";
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieList() {
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
			Statement genreStatement = dbcon.createStatement();
			Statement starStatement = dbcon.createStatement();
			String query = "SELECT DISTINCT M.id, M.title, M.year, M.director, R.rating "
					     + "FROM movies M,ratings R "
					     + "WHERE M.id = R.movieId "
					     + "ORDER BY rating DESC LIMIT 20"; 
			ResultSet rs = statement.executeQuery(query);
			
			JsonArray jsonArray = new JsonArray();
			
			while (rs.next()) {
				String m_id = rs.getString("M.id");
				String m_title = rs.getString("M.title");
				String m_year  = rs.getString("M.year");
				String m_director = rs.getString("M.director");
				String genreList = createQueryList(genreStatement, m_title, "G.name");
				String starList = createQueryList(starStatement, m_title, "S.name");
				String r_rating = rs.getString("R.rating");
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", m_id);
				jsonObject.addProperty("movie_title", m_title);
				jsonObject.addProperty("movie_year", m_year);
				jsonObject.addProperty("movie_director", m_director);
				jsonObject.addProperty("genre_list", genreList);
				jsonObject.addProperty("star_list", starList);
				jsonObject.addProperty("ratings_rating", r_rating);
				
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
	
	public String createGenreQuery(String movieTitle) {
		return "SELECT G.name " +
			   "FROM movies M, genres_in_movies GIM, genres G " +
		       "WHERE M.title = \"" + movieTitle + "\" AND M.id = GIM.movieId AND G.id = GIM.genreId";
	}
	
	public String createStarQuery(String movieTitle) {
		return "SELECT S.name " +
		       "FROM movies M, stars_in_movies SIM ,stars S " +
		       "WHERE M.title = \"" + movieTitle + "\" AND M.id = SIM.movieId AND S.id = SIM.starId";
	}
	
	public String createQueryList(Statement queryStatement, String movieTitle, String selectName) {
		String myQuery = "";
		String selectList = "";
		if (selectName == "G.name")
			myQuery += createGenreQuery(movieTitle);
		else
			myQuery += createStarQuery(movieTitle);
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
