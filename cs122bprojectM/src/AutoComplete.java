import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

@WebServlet("/AutoComplete")
public class AutoComplete extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AutoComplete() {
		super();
	}
	
	public static HashMap<String, String> moviesMap = new HashMap<>();
	public static HashMap<String, String> starsMap = new HashMap<>();

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
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/MovieDB";
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement movieStatement = dbcon.createStatement();
			Statement starStatement = dbcon.createStatement();
			
			createMovieJson(query, movieStatement, jsonArray);
			createStarJson(query, starStatement, jsonArray);
			
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
	
	public void createMovieJson(String title, Statement movieStatement, JsonArray jArray) throws SQLException
	{
		String[] splitTitle = title.split(" "); 
		String movieQuery = "SELECT id, title "
				+ "FROM movies " 
				+ "WHERE MATCH(id, title) against(";
		for(int i = 0; i < splitTitle.length; i++) {
			String token = "\'+" + splitTitle[i] + "*\' ";
			movieQuery += token; 
		}
		
		movieQuery += "IN BOOLEAN MODE) LIMIT 5";

		ResultSet rs = movieStatement.executeQuery(movieQuery);


		while (rs.next()) {
			String m_id = rs.getString("id");
			String m_title = rs.getString("title");
			jArray.add(generateJsonObject(m_id, m_title, "Movie"));
		}
		rs.close();
	}
	
	public void createStarJson(String name, Statement starStatement, JsonArray jArray) throws SQLException
	{
		String[] splitName = name.split(" "); 
		String starQuery = "SELECT id, name "
				+ "FROM stars " 
				+ "WHERE MATCH(id, name) against(";
		for(int i = 0; i < splitName.length; i++) {
			String token = "\'+" + splitName[i] + "*\' ";
			starQuery += token; 
		}
		
		starQuery += "IN BOOLEAN MODE) LIMIT 5";

		ResultSet rs = starStatement.executeQuery(starQuery);


		while (rs.next()) {
			String s_id = rs.getString("id");
			String s_name = rs.getString("name");
			jArray.add(generateJsonObject(s_id, s_name, "Star"));
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