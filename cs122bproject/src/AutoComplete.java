import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.HashMap;

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

@WebServlet("/AutoComplete")
public class AutoComplete extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection dbcon = null;
	private PreparedStatement movieStatement = null;
	private PreparedStatement starStatement = null;
	
	public static HashMap<String, String> moviesMap = new HashMap<>();
	public static HashMap<String, String> starsMap = new HashMap<>();
	
	public AutoComplete() {
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonArray jsonArray = new JsonArray();
		String query = request.getParameter("query");
		
		if (query == null || query.trim().isEmpty()) 
		{
			response.getWriter().write(jsonArray.toString());
			return;
		}
	
		response.setContentType("application/html");
		PrintWriter out = response.getWriter();
		
		String movieString = createMovieQuery();
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
			movieStatement = dbcon.prepareStatement(movieString);
			starStatement = dbcon.prepareStatement(starString);
			
			createJson(query, movieStatement, jsonArray, "Movie");
			createJson(query, starStatement, jsonArray, "Star");
			
			response.getWriter().write(jsonArray.toString());
			movieStatement.close();
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public String createMovieQuery() {
		return "SELECT id, title " +
			   "FROM movies " +
			   "WHERE MATCH(id, title) AGAINST(? IN BOOLEAN MODE) " +
			   "LIMIT 5";
	}
	
	public String createStarQuery() {
		return "SELECT id, name " +
			   "FROM stars " +
			   "WHERE MATCH(id, name) AGAINST(? IN BOOLEAN MODE) " +
			   "LIMIT 5";
	}
	
	public String createFullTextQuery(String searchParam) {
		String query = "";
		String[] splitParam = searchParam.split(" ");
		for(int i = 0; i < splitParam.length; i++) {
			query += "+" + splitParam[i] + "* "; 
		}
		return query;
	}
	
	public String makeName(String category) {
		String name = "";
		if (category.equals("Movie")) {
			name += "title";
		}
		else if (category.equals("Star")) {
			name += "name";
		}
		return name;
	}
	
	public void createJson(String query, PreparedStatement statement, JsonArray jArray, String category) throws SQLException {
		String FTQuery = createFullTextQuery(query);
		statement.setString(1, FTQuery);
		ResultSet rs = statement.executeQuery();
		dbcon.commit();
		String name = makeName(category);
		while (rs.next()) {
			String m_id = rs.getString("id");
			String m_name = rs.getString(name);
			jArray.add(generateJsonObject(m_id, m_name, category));
		}
		rs.close();
	}
	
	private static JsonObject generateJsonObject(String id, String name, String categoryName)
	{
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", name);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("category", categoryName);
		additionalDataJsonObject.addProperty("m_id", id);

		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}
}