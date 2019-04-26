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

public class SAXParserDirectorFilms extends DefaultHandler {

	List<Film> myFilms;
	List<String> tempGenres;
	private String tempVal;
	private String tempDir;
	// to maintain context
	private Film tempFilm;

	public SAXParserDirectorFilms() {
		myFilms = new ArrayList<Film>();
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
			sp.parse("../../stanford-movies/mains243.xml", this);

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

		System.out.println("No of Employees '" + myFilms.size() + "'.");

		Iterator<Film> it = myFilms.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}
	*/
	
	// Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// reset
		tempVal = "";
		if (qName.equalsIgnoreCase("film")) {
			// create a new instance of employee
			tempFilm = new Film();
		} else if (qName.equalsIgnoreCase("cats")) {
			tempGenres = new ArrayList<String>();
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equalsIgnoreCase("film")) {
			// add it to the list
			tempFilm.setDirector(tempDir);
			myFilms.add(tempFilm);

		} else if (qName.equalsIgnoreCase("dirname")) {
			tempDir = tempVal;
		} else if (qName.equalsIgnoreCase("fid") || qName.equalsIgnoreCase("filmed")) {
			tempFilm.setId(tempVal);
		} else if (qName.equalsIgnoreCase("t")) {
			tempFilm.setTitle(tempVal);
		} else if (qName.equalsIgnoreCase("year")) {
			tempFilm.setYear(checkFilmYear(tempVal));
		} else if (qName.equalsIgnoreCase("cat")) {
			tempVal = tempFilm.checkGenre(tempVal.trim());
			tempGenres.add(tempVal);
		} else if (qName.equalsIgnoreCase("cats")) {
			tempFilm.setGenres(tempGenres);
		}
	}
	
	public int checkFilmYear(String tempValue) {
		int tempYear;
		try {
			tempYear = Integer.parseInt(tempValue);
		} catch (NumberFormatException nfe) {
			tempYear = 1950;
		}
		return tempYear;
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
			Statement genreStatement = dbcon.createStatement();
			Statement GIMStatement = dbcon.createStatement();
			
			String insertMovieQuery = "INSERT INTO movies (id, title, year, director)" +
					   " VALUES(?, ?, ?, ?)"; 
			String insertGenreQuery = "INSERT INTO genres (name)" +
					   " VALUES(?)";
			String insertGIMQuery = "INSERT INTO genres_in_movies (genreId, movieId)" +
					   " VALUES(?, ?)";
			String insertRatingQuery = "INSERT INTO ratings (movieId, rating, numVotes)" +
					   " VALUES(?, ?, ?)";
			
			Iterator<Film> it = myFilms.iterator();
			while (it.hasNext()) {
				PreparedStatement insertMovieStatement = dbcon.prepareStatement(insertMovieQuery);
				Film myFilm = it.next();
				String movieId = myFilm.getId();
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
					
					myFilm.setId(tempId);
					movieId = tempId;
				}
				insertMovieStatement.setString(1, movieId);
				insertMovieStatement.setString(2, myFilm.getTitle());
				insertMovieStatement.setInt(3, myFilm.getYear());
				insertMovieStatement.setString(4, myFilm.getDirector());
				insertMovieStatement.executeUpdate();
				insertMovieStatement.close();
				
				Iterator<String> iter = myFilm.getGenres().iterator();
				while (iter.hasNext()) {
					String aGenre = iter.next();
					if (!(checkGenre(genreStatement, aGenre))) {
						PreparedStatement insertGenreStatement = dbcon.prepareStatement(insertGenreQuery);
						insertGenreStatement.setString(1, aGenre);
						insertGenreStatement.executeUpdate();
						insertGenreStatement.close();
					}
					int genreId = getGenreId(GIMStatement, aGenre);
					PreparedStatement insertGIMStatement = dbcon.prepareStatement(insertGIMQuery);
					insertGIMStatement.setInt(1, genreId);
					insertGIMStatement.setString(2, movieId);
					insertGIMStatement.executeUpdate();
					insertGIMStatement.close();
				}
				PreparedStatement insertRatingStatement = dbcon.prepareStatement(insertRatingQuery);
				insertRatingStatement.setString(1, movieId);
				insertRatingStatement.setFloat(2, 5.5f);
				insertRatingStatement.setInt(3, 100);
				insertRatingStatement.executeUpdate();
				insertRatingStatement.close();
			}
			
			GIMStatement.close();
			genreStatement.close();
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
	
	public boolean checkGenre(Statement statement, String genre) {
		boolean genreExists = false;
		String query = "SELECT name FROM genres WHERE name = \"" + genre + "\"";
		try {
			ResultSet rs = statement.executeQuery(query);
			if(rs.first()) {
				genreExists = true;
			}
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return genreExists;
	}
	
	public int getGenreId(Statement statement, String genre) {
		int genreId = -1;
		String query = "SELECT id FROM genres WHERE name = \"" + genre + "\"";
		try {
			ResultSet rs = statement.executeQuery(query);
			if(rs.first()) {
				genreId = Integer.parseInt(rs.getString("id"));
			}
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return genreId;
	}
	
	public static void main(String[] args) {
		SAXParserDirectorFilms spdf = new SAXParserDirectorFilms();
		spdf.runExample();
	}
}
