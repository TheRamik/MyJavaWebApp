import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String filename = "/home/ubuntu/cs122b-winter2018-team-84/cs122bproject/WebContent/TimeResults/newSearchLog.txt";
	protected PreparedStatement statement = null;
	protected PreparedStatement genreStatement = null;
	protected PreparedStatement starStatement = null;
	protected Connection dbcon = null;
	protected String sortByTitle = null;
	protected String sortByYear = null;
	protected long startTime = 0;
	protected long endTime = 0;
	
	public Search() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long totalStartTime = System.nanoTime();
		String searchParam = request.getParameter("search");
		String items = request.getParameter("items");
		String page = request.getParameter("page");
		sortByTitle = request.getParameter("sortTitle");
		sortByYear = request.getParameter("sortYear");
		
		File file = new File(filename);
		
		if (!file.exists()) {
			file.createNewFile();
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		FileWriter logFile = new FileWriter(filename, true);
		
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/MovieDB";
		
		PrintWriter printWriter = new PrintWriter(logFile);
		String selectString = createStatementQuery();
		String genreString = createGenreQuery();
		String starString = createStarQuery();
		
		try {
			startTime = System.nanoTime();
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// Connection Pooling
			startTime = System.nanoTime();
			Context initCtx = new InitialContext(); 
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
            if (ds == null)
                out.println("ds is null.");

            dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");
            endTime = System.nanoTime();
            long cpElapsedTime = endTime - startTime;
            //Connection Pooling End
            
            startTime = System.nanoTime(); // Start Timer for Prepared Statement
            dbcon.setAutoCommit(false);
			statement = dbcon.prepareStatement(selectString);
			genreStatement = dbcon.prepareStatement(genreString);
			starStatement = dbcon.prepareStatement(starString);
         
			String query = createFullTextQuery(searchParam);
			int newItems = createItems(items);
			int offset = createOffset(newItems, page);
			statement.setString(1, query);
			statement.setInt(2,  newItems);
			statement.setInt(3, offset);
			
			ResultSet rs = statement.executeQuery();
			dbcon.commit();
			endTime = System.nanoTime();
			long psElapsedTime = endTime - startTime;
			JsonArray jsonArray = new JsonArray();
			long gpsElapsedTime = 0;
			long spsElapsedTime = 0;
			int count = 0;
			while (rs.next()) {
				count++;
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
			genreStatement.close();
			starStatement.close();
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
	
	public int createItems(String items) {
		if (items == null) {
			items = "10";
		}
		int intItems = Integer.parseInt(items);
		return intItems; 
	}
	
	public int createOffset(int items, String page) {
		if (page == null) {
			page = "1";
		}
		int offset = (Integer.parseInt(page) - 1) * items;
		return offset;
	}
	
	public String createFullTextQuery(String searchParam) {
		String query = "";
		String[] splitParam = searchParam.split(" ");
		for(int i = 0; i < splitParam.length; i++) {
			query += "+" + splitParam[i] + "* "; 
		}
		return query;
	}
	
	public String appendOrderBy(String sortTitle, String sortYear) {
		if (sortTitle != null) {
			return "ORDER BY title "; 
		}
		else {
			return "ORDER BY year DESC ";
		}
	}
	
	public String createStatementQuery() {
		String query = "SELECT id, title, year, director " +
	  			  	   "FROM movies " +
	  			  	   "WHERE MATCH(id, title) AGAINST(? IN BOOLEAN MODE) ";
		
		if(sortByTitle != null || sortByYear != null) {
			query += appendOrderBy(sortByTitle, sortByYear);
		}
		query += "LIMIT ? OFFSET ?";
		return query;
	}
	
	public String createGenreQuery() {
		return "SELECT G.name " +
			   "FROM movies M, genres_in_movies GIM, genres G " +
		       "WHERE M.title = ? AND M.id = GIM.movieId AND G.id = GIM.genreId";
	}
	
	public String createStarQuery() {
		return "SELECT S.name " +
		       "FROM movies M, stars_in_movies SIM ,stars S " +
		       "WHERE M.title = ? AND M.id = SIM.movieId AND S.id = SIM.starId";
	}
	
	public String createQueryList(PreparedStatement queryStatement, String movieTitle, String selectName) throws SQLException {
		String selectList = "";
		queryStatement.setString(1, movieTitle);
		ResultSet aRS = queryStatement.executeQuery();
		dbcon.commit();
		selectList += resultSetToString(aRS, selectName);
		aRS.close();
		return selectList;
	}
	
	public String resultSetToString(ResultSet rSet, String selectName) throws SQLException{
		String resultList = "";
		while (rSet.next()) {
			resultList += rSet.getString(selectName);
			if (!rSet.isLast())
				resultList += ", ";
		}
		return resultList;
	}
}