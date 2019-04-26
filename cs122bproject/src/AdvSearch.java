import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

@WebServlet("/AdvSearch")
public class AdvSearch extends Search {
	private static final long serialVersionUID = 1L;
	
	private String title = null;
	private String year = null;
	private String director = null;
	private String name = null;
	
	public AdvSearch() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		title = request.getParameter("title");
		year = request.getParameter("year");
		director = request.getParameter("director");
		name = request.getParameter("name");
		String items = request.getParameter("items");
		String page = request.getParameter("page");
		sortByTitle = request.getParameter("sortTitle");
		sortByYear = request.getParameter("sortYear");
	
		response.setContentType("application/html");
		PrintWriter out = response.getWriter();
		
		String selectString = createStatementQuery();
		String genreString = createGenreQuery();
		String starString = createStarQuery();
		
		try {
			//Connection Pooling
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
            //Connection Pooling End
			
            dbcon.setAutoCommit(false);
            statement = dbcon.prepareStatement(selectString);
            genreStatement = dbcon.prepareStatement(genreString);
            starStatement = dbcon.prepareStatement(starString);            
            
            int newItems = createItems(items);
			int offset = createOffset(newItems, page);
			setQuery(newItems, offset);
			
			ResultSet rs = statement.executeQuery();
			dbcon.commit();
			JsonArray jsonArray = new JsonArray();
			
			while (rs.next()) {
				String m_id = rs.getString("M.id");
				String m_title = rs.getString("M.title");
				int m_year  = rs.getInt("M.year");
				String m_director = rs.getString("M.director");
				String genreList = createQueryList(genreStatement, m_title, "G.name");
				String starList = createQueryList(starStatement, m_title, "S.name");
				
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
			rs.close();
			statement.close();
			genreStatement.close();
			starStatement.close();
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
	
	public String createStatementQuery() {
		String query = "SELECT DISTINCT M.id, M.title, M.year, M.director ";
		if (!name.isEmpty()) {
			query += "FROM movies M, stars S, stars_in_movies SIM WHERE ";
		} 
		else {
			query += "FROM movies M WHERE ";
		}
		
		query += appendQuery(title, year, director, name);
		
		if(sortByTitle != null || sortByYear != null) {
			query += appendOrderBy(sortByTitle, sortByYear);
		}
		query += "LIMIT ? OFFSET ?";
		return query;
	}
	
	public String appendQuery(String myTitle, String myYear, String myDir, String myName) {
		String query = "";
		if (!myTitle.isEmpty()) {
			String titleConstraint = " M.title = ?";
			query += titleConstraint;
		}
		if (!myYear.isEmpty()) {
			String yearConstraint = " M.year = ?";
			if (myTitle != "")
				query += " AND";
			query += yearConstraint;
		}
		if (!myDir.isEmpty()) {
			String directorConstraint = " M.director = ?";
			if (myTitle != "" || myYear != "")
				query += " AND";
			query += directorConstraint;
		}
		if (!myName.isEmpty()) {
			String nameConstraint = " S.name = ?";
			if (myTitle != "" || myYear != "" || myDir != "")
				query += " AND";
			query += nameConstraint;
			query += "AND SIM.movieId = M.id AND SIM.starId = S.id";
		}
		return query;
	}
	
	public void setQuery(int items, int offset) throws SQLException {
		int count = 0;
		if (!title.isEmpty()) {
			count++;
			statement.setString(count, title);
		}
		if (!year.isEmpty()) {
			count++;
			int intYear = Integer.parseInt(year);
			statement.setInt(count, intYear);
		}
		if (!director.isEmpty()) {
			count++;
			statement.setString(count, director);
		}
		if (!name.isEmpty()) {
			count++;
			statement.setString(count, name);
		}
		count++;
		statement.setInt(count, items);
		count++;
		statement.setInt(count, offset);
	}
	
}