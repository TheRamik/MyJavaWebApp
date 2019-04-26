import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

@WebServlet("/RegularSearch")
public class RegularSearch extends Search {
	private static final long serialVersionUID = 1L;
	
	private static final String filename = "/home/ubuntu/cs122b-winter2018-team-84/cs122bproject/WebContent/TimeResults/newRSearchLog.txt";
	
	
	public RegularSearch() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long totalStartTime = System.nanoTime();
		String searchParam = request.getParameter("search");
		String items = request.getParameter("items");
		String page = request.getParameter("page");
		String sortByTitle = request.getParameter("sortTitle");
		String sortByYear = request.getParameter("sortYear");
	
		File file = new File(filename);
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		response.setContentType("application/html");
		PrintWriter out = response.getWriter();
		FileWriter logFile = new FileWriter(filename, true);
		
		PrintWriter printWriter = new PrintWriter(logFile);
		
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/MovieDB";
		
		try {
			startTime = System.nanoTime();
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			endTime = System.nanoTime();
			long cpElapsedTime = endTime - startTime;    // end of timer for connection
			startTime = System.nanoTime();              // start timer for statement
			Statement statement = dbcon.createStatement();
			Statement genreStatement = dbcon.createStatement();
			Statement starStatement = dbcon.createStatement();
			endTime = System.nanoTime();               // end of timer for statement
			long psElapsedTime = endTime - startTime;
			String[] splitParam = searchParam.split(" "); 
			String query = "SELECT id, title, year, director "
					+ "FROM movies " 
					+ "WHERE MATCH(id, title) against(";
			for(int i = 0; i < splitParam.length; i++) {
				String token = "\'+" + splitParam[i] + "*\' ";
				query += token; 
			}
			
			query += "IN BOOLEAN MODE) ";
			
			if(sortByTitle != null || sortByYear != null) {
				query += appendOrderBy(sortByTitle, sortByYear);
			}
			query += appendLimitAndOffset(page, items);
			
			startTime = System.nanoTime();
			ResultSet rs = statement.executeQuery(query);
			endTime = System.nanoTime();
			psElapsedTime += endTime - startTime;
			JsonArray jsonArray = new JsonArray();
			long gpsElapsedTime = 0;
			long spsElapsedTime = 0;
			int count = 0;
			while (rs.next()) {
				String m_id = rs.getString("id");
				String m_title = rs.getString("title");
				int m_year  = rs.getInt("year");
				String m_director = rs.getString("director");
				startTime = System.nanoTime();
				String genreList = createQueryList(genreStatement, m_title, "G.name");
				endTime = System.nanoTime();
				gpsElapsedTime += endTime - startTime;
				startTime = System.nanoTime();
				String starList = createQueryList(starStatement, m_title, "S.name");
				endTime = System.nanoTime();
				spsElapsedTime += endTime - startTime;
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", m_id);
				jsonObject.addProperty("movie_title", m_title);
				jsonObject.addProperty("movie_year", m_year);
				jsonObject.addProperty("movie_director", m_director);
				jsonObject.addProperty("genre_list", genreList);
				jsonObject.addProperty("star_list", starList);
				
				jsonArray.add(jsonObject);
			}
			
			out.write(jsonArray.toString());
			startTime = System.nanoTime();
			rs.close();
			statement.close();
			dbcon.close();
			endTime = System.nanoTime();
			psElapsedTime += endTime - startTime;
			if (count == 0) {
				count++;
			}
			long avgjdbcElapsedTime = cpElapsedTime + psElapsedTime + (gpsElapsedTime/count) + (spsElapsedTime/count);
			long totalEndTime = System.nanoTime();
			long totalElapsedTime = totalEndTime - totalStartTime;
			printWriter.println("TS = " + totalElapsedTime + ", TJ = " + avgjdbcElapsedTime);
			printWriter.close();
			
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
	
	public String appendLimitAndOffset(String page, String items) {
		if (page == null) {
			page = "1";
		}
		int intItems = createItems(items);
		int intOffset = createOffset(intItems, page);
		String offset = Integer.toString(intOffset);
		String strItems = Integer.toString(intItems);
		return "LIMIT " + strItems + " OFFSET " + offset;
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
	
	public String createQueryList(Statement queryStatement, String movieTitle, String selectName) throws SQLException {
		String myQuery = "";
		String selectList = "";
		if (selectName.equals("G.name")) {

			myQuery += createGenreQuery(movieTitle);
		}
		else
			myQuery += createStarQuery(movieTitle);
		ResultSet aRS = queryStatement.executeQuery(myQuery);
		selectList += resultSetToString(aRS, selectName);
		aRS.close();
		return selectList;
	}
	
	
	public String resultSetToString(ResultSet rSet, String selectName) throws SQLException {
		String resultList = "";
		while (rSet.next()) {
			resultList += rSet.getString(selectName);
			if (!rSet.isLast())
				resultList += ", ";
		}
		return resultList;
	}
}