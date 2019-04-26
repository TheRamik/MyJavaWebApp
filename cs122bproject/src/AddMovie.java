import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class MovieList
 */
@WebServlet("/AddMovie")
public class AddMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public String getServletInfo() {
		return "Servlet connects to MySQL database and displays result of a SELECT";
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddMovie() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		String movie_title = request.getParameter("movieTitle");
		String movie_year = request.getParameter("movieYear");
		String movie_dir = request.getParameter("director");
		String movie_genre = request.getParameter("genre");
		String movie_star = request.getParameter("starName");
		String movie_staryear = request.getParameter("birthYear");
		
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
			
			Statement IDStatement = dbcon.createStatement();
			Statement starStatement = dbcon.createStatement();
			Statement MIDStatement = dbcon.createStatement();
			Statement SIDStatement = dbcon.createStatement();
			Statement movieStatement = dbcon.createStatement();
			Statement genreStatement = dbcon.createStatement();
			
			if(movie_year == "") {
				getMovieInfo(movieStatement, movie_title, "year");
			}
			
			if(movie_dir == "") {
				getMovieInfo(movieStatement, movie_title, "director");
			}
			
			if(movie_genre == "") {
				getGenre(genreStatement, movie_title);
			}
			
			if(movie_star == "") {
				movie_star = null;
			}
			
			if(movie_staryear == "") {
				movie_staryear = null;
			}
			
			String movie_id = getID(IDStatement, "movies", "title", movie_title);
			String star_id = getID(starStatement, "stars", "name", movie_star);
			String new_movie_id = getNewID(MIDStatement, "movies");
			String new_star_id = getNewID(SIDStatement, "stars");

			CallableStatement addMovieProcedure = dbcon.prepareCall("{call add_movie(?,?,?,?,?,?,?,?,?,?)}");
			
			addMovieProcedure.setString(1, movie_id);
			addMovieProcedure.setString(2, movie_title);
			addMovieProcedure.setInt(3, Integer.parseInt(movie_year));
			addMovieProcedure.setString(4, movie_dir);
			addMovieProcedure.setString(5, movie_star);
			addMovieProcedure.setString(6, star_id);
			addMovieProcedure.setString(7, movie_staryear);
			addMovieProcedure.setString(8, movie_genre);
			addMovieProcedure.setString(9, new_movie_id);
			addMovieProcedure.setString(10, new_star_id);

			addMovieProcedure.execute();
			
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("status", "success");
			responseJsonObject.addProperty("message", "success");
		
			out.write(responseJsonObject.toString());
			
			addMovieProcedure.close();
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
	
	public String getNewID(Statement queryStatement, String table) {
		String newID = "";
		String stringID = "";
		String IDquery = "SELECT max(id) FROM " + table;
		int num;
		try {
			ResultSet rs = queryStatement.executeQuery(IDquery);
			rs.first();
			stringID = rs.getString("max(id)");
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
	
	public String getID(Statement queryStatement, String table, String name, String id) {
		String stringID = "";
		String IDquery = "SELECT id FROM " + table + " WHERE " + name + " = \'" + id + "\'";
		try {
			ResultSet rs = queryStatement.executeQuery(IDquery);
			rs.first();
			stringID = rs.getString("id");
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return stringID;
	}
	
	public String getMovieInfo(Statement queryStatement, String movie, String info) {
		String movieInfo = "";
		String IDquery = "SELECT " + info + " FROM movies WHERE title = \'" + movie + "\'";
		try {
			ResultSet rs = queryStatement.executeQuery(IDquery);
			rs.first();
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return movieInfo;
	}
	
	public String getGenre(Statement queryStatement, String info) {
		String movieInfo = "";
		String IDquery = "SELECT G.name FROM movies M, genres_in_movies GIM, genres G " +
						"WHERE M.title = \"" + info + "\" AND M.id = GIM.movieId AND G.id = GIM.genreId";
		try {
			ResultSet rs = queryStatement.executeQuery(IDquery);
			rs.first();
			movieInfo = rs.getString(info);
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return movieInfo;
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
