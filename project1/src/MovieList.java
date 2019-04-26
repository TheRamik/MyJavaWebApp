

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MovieList
 */
//@WebServlet("/MovieList")
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
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/MovieDB";
		
		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
		
		printBeginningOfPage(out);
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement statement = dbcon.createStatement();
			Statement genreStatement = dbcon.createStatement();
			Statement starStatement = dbcon.createStatement();
			String query = "SELECT DISTINCT M.title, M.year, M.director, R.rating "
					     + "FROM movies M, ratings R "
					     + "WHERE M.id = R.movieId "
					     + "ORDER BY rating DESC LIMIT 20"; 
			ResultSet rs = statement.executeQuery(query);
			out.println("<TABLE id=\"topMovies\">\r\n");
			
			out.println("<tr>" + "<th>Title</th>" + "<th>Year</th>" 
					   + "<th>Director</th>" + "<th>Genres</th>" 
	                   + "<th>Stars</th>" + "<th>Rating</th>" 
					   + "</tr>\r\n");
			
			while (rs.next()) {
				String m_title = rs.getString("M.title");
				String m_year  = rs.getString("M.year");
				String m_director = rs.getString("M.director");
				String genreList = createQueryList(genreStatement, m_title, "G.name");
				String starList = createQueryList(starStatement, m_title, "S.name");
				String r_rating = rs.getString("R.rating");
				
				out.println("<tr>" + "<td>" + m_title + "</td>" + "<td>" + m_year + "</td>" 
								   + "<td>" + m_director + "</td>" + "<td>" + genreList + "</td>" 
				                   + "<td>" + starList + "</td>" + "<td>" + r_rating + "</td>" 
								   + "</tr>\r\n");
			}
			
			printEndOfPage(out);
			
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
	
	
	public void printBeginningOfPage(PrintWriter out) {
		out.println("<HTML>\r\n" +
				"<HEAD><TITLE>UCIMDb Top 20 - UCIMDb</TITLE>\r\n" +
				"   <link rel=\"stylesheet\" href=\"../styles/naviStyle.css\">\r\n" +
				"   <link rel=\"stylesheet\" href=\"../styles/topMovies.css\">\r\n" + 
				"	<link rel=\"stylesheet\" href=\"../styles/footerStyle.css\">\r\n" +
				"	<link rel=\"stylesheet\" href=\"../styles/styles.css\">\r\n" +
				"	<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\r\n" + 
				"	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n" + 
				"</HEAD>\n");
		out.println("<BODY><header>\r\n" + 
				"	<div class=\"topnav\" id=\"myTopnav\">\r\n" + 
				"  		<a href=\"..\" class=\"active\"><i class=\"fa fa-home\"></i>Home</a>\n" + 
				"  		<a href=\"project1\"><i class=\"fa fa-id-card\"></i>Top Rated Movies</a>\n" + 
				"  		<a href=\"javascript:void(0);\" class=\"icon\" onclick=\"changeNavi()\">&#9776;</a>\n" + 
				"	</div>\n" + 
				"</header>\n");
		out.println("<H1>Top Rated Movies</H1>\r\n");
	}
	
	public void printEndOfPage(PrintWriter out) {
		out.println("</TABLE>\r\n");
		out.println("<br/><br/>\r\n");
		out.println("<footer>\r\n" + 
				"	<div class=\"footer-social\">\r\n" + 
				"		<div class=\"footer-social-inside\">\r\n" +  
				"			<a target=\"_blank\" href=\"http://www.imdb.com/?ref_=nv_home\" title=\"IMDb\">\r\n" + 
				"				<i class=\"fa fa-imdb\" aria-hidden=\"true\"></i>\r\n" + 
				"				<span>IMDb</span>\r\n" + 
				"			</a>\r\n" + 
				"		</div>\r\n" + 
				"	</div>\r\n" + 
				"</footer>\r\n");
		out.println("<p>This project is presented by Ricky Tham and Hayley Tse :)</p>\n");
		out.println("</body></html>");
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
}
