import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserCast extends DefaultHandler {

	List<Casts> myCasts;
	List<String> tempStars;
	private String tempVal;
	private String tempDir;
	// to maintain context
	private Casts tempCasts;
	private int tempIdNum;

	public SAXParserCast() {
		myCasts = new ArrayList<Casts>();
		tempIdNum = getNumOfStars();
	}

	public void runExample() {
		parseDocument();
		//printData();
		insertData();
	}

	private void parseDocument() {

		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			// parse the file and also register this class for call backs
			sp.parse("../../stanford-movies/casts124.xml", this);
//			sp.parse("casts124.xml", this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * Iterate through the list and print the contents
	private void printData() {

		System.out.println("No of Employees '" + myCasts.size() + "'.");

		Iterator<Casts> it = myCasts.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
		
		System.out.println("FINISH");
	}
	*/
	
	public int getNumOfStars() {
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/MovieDB";
		int num = 0; 
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			Connection conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement numStarsStatement = conn.createStatement();
			
			String numStarsQuery = "SELECT COUNT(*) FROM stars";
			
			ResultSet rs = numStarsStatement.executeQuery(numStarsQuery);
			rs.first();
			num = rs.getInt("COUNT(*)");
			
		} catch (SQLException ex) {
			while (ex != null) {
			System.out.println("SQL Exception:   " + ex.getMessage());
			ex = ex.getNextException();
			} 
		} catch (java.lang.Exception ex) {
		System.out.println(ex.getMessage());
	}
	return num;
}
	
	// Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// reset
		tempVal = "";
		if (qName.equalsIgnoreCase("filmc")) {
			// create a new instance of employee
			tempCasts = new Casts(); 
			tempStars = new ArrayList<String>();	
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equalsIgnoreCase("filmc")) {
			// add it to the list
			tempCasts.setGenres(tempStars);
			tempCasts.setDirector(tempDir);
			myCasts.add(tempCasts);

		} else if (qName.equalsIgnoreCase("is")) {
			tempDir = tempVal;
		} else if (qName.equalsIgnoreCase("f")) {
			tempCasts.setId(tempVal);
		} else if (qName.equalsIgnoreCase("t")) {
			tempCasts.setTitle(tempVal);
		} else if (qName.equalsIgnoreCase("a")) {
			tempStars.add(tempVal);
		}
	}
	
	// Handle SQL database
	private void insertData() {
		
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/MovieDB";
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement movieStatement = dbcon.createStatement();
			Statement starStatement = dbcon.createStatement();
			Statement SIMStatement = dbcon.createStatement();
			Statement SYStatement = dbcon.createStatement();
			Statement MYStatement = dbcon.createStatement();
			
			String insertMovieQuery = "INSERT INTO movies (id, title, year, director)" +
					   " VALUES(?, ?, ?, ?)"; 
			String insertStarQuery = "INSERT INTO stars (id, name, birthYear)" +
					   " VALUES(?, ?, ?)"; 
			String insertSIMQuery = "INSERT INTO stars_in_movies (starId, movieId)" +
					   " VALUES(?, ?)";

			Iterator<Casts> it = myCasts.iterator();
			while (it.hasNext()) {
				PreparedStatement insertMovieStatement = dbcon.prepareStatement(insertMovieQuery);
				Casts myCast = it.next();
				String movieId = myCast.getId();
				while (movieId == null || movieId.isEmpty() || checkMovieId(movieStatement, movieId)) {
					String tempId;
					if (!movieId.isEmpty() && movieId.matches("^.*\\d$")) {
						String tempNum = movieId.substring(movieId.length() - 1);
						tempNum = String.valueOf(Integer.parseInt(tempNum) + 1);
						tempId = movieId.substring(0, movieId.length() - 1) + tempNum;
					}
					else {
						tempId = movieId + "1";
					}
					
					myCast.setId(tempId);
					movieId = tempId;
				}
				insertMovieStatement.setString(1, movieId);
				insertMovieStatement.setString(2, myCast.getTitle());
				insertMovieStatement.setInt(3, getMovieYear(MYStatement, movieId));
				insertMovieStatement.setString(4, myCast.getDirector());
				insertMovieStatement.executeUpdate();
				insertMovieStatement.close();
				
				Iterator<String> iter = myCast.getStars().iterator();
				while (iter.hasNext()) {
					String aStar = iter.next();
					if (aStar.matches("^.*\\.*$")) {
						aStar = aStar.replace("\\","");
					} else if(aStar.matches("^.*\\\'.*$")) {
						String[] tempStarNm = aStar.split("\\'");
						String tempNme = "";
						for (String nm: tempStarNm) {
							tempNme += nm;
						}
						aStar = tempNme;
					} 
					if (aStar.matches("^.*\".*$")) {
						aStar = aStar.replace("\"", "");
					}
					if (!(checkStar(starStatement, aStar))) {
						PreparedStatement insertStarStatement = dbcon.prepareStatement(insertStarQuery);
						insertStarStatement.setString(1, getStarId(SIMStatement, aStar));
						insertStarStatement.setString(2, aStar);
						insertStarStatement.setInt(3, getStarYear(SYStatement, aStar));
						insertStarStatement.executeUpdate();
						insertStarStatement.close();
					}
					String starId = getStarId(SIMStatement, aStar);
					PreparedStatement insertSIMStatement = dbcon.prepareStatement(insertSIMQuery);
					insertSIMStatement.setString(1, starId);
					insertSIMStatement.setString(2, movieId);
					insertSIMStatement.executeUpdate();
					insertSIMStatement.close();
				}
			}
			
			SIMStatement.close();
			starStatement.close();
			movieStatement.close();
			dbcon.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (java.lang.Exception ex) {
			System.out.println(ex.getMessage());
			return;
		}
		
	}
	
	public boolean checkMovieId(Statement statement, String movId) {
		boolean idExists = false;
		String query = "SELECT id FROM movies WHERE id = \"" + movId + "\"";
		try {
			ResultSet rs = statement.executeQuery(query);
			if(rs.first()) {
				idExists = true;
			}
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return idExists;
	}
	
	public boolean checkStar(Statement statement, String star) {
		boolean starExists = false;
		String query = "SELECT name FROM stars WHERE name = \"" + star + "\"";
		try {
			ResultSet rs = statement.executeQuery(query);
			if(rs.first()) {
				starExists = true;
			}
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return starExists;
	}
	
	public String getStarId(Statement statement, String star) {
		String starId = "";
		String query = "SELECT id FROM stars WHERE name = \"" + star + "\"";
		try {
			ResultSet rs = statement.executeQuery(query);
			if(rs.first()) {
				starId = rs.getString("id");
			}
			else {
				tempIdNum += 1;
				starId += "rt" + String.valueOf(tempIdNum);
			}
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return starId;
	}
	
	public int getStarYear(Statement statement, String star) {
		int starYear = 0;
		String query = "SELECT birthYear FROM stars WHERE name = \"" + star + "\"";
		try {
			ResultSet rs = statement.executeQuery(query);
			if(rs.first()) {
				starYear = Integer.parseInt(rs.getString("birthYear"));
			}
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return starYear;
	}
	
	public int getMovieYear(Statement statement, String movId) {
		int movieYear = 0;
		String query = "SELECT year FROM movies WHERE id = \"" + movId + "\"";
		try {
			ResultSet rs = statement.executeQuery(query);
			if(rs.first()) {
				movieYear = Integer.parseInt(rs.getString("year"));
			}
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return movieYear;
	}
	
	public static void main(String[] args) {
		SAXParserCast spdf = new SAXParserCast();
		spdf.runExample();
	}
}
