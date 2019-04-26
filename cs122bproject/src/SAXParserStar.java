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

public class SAXParserStar extends DefaultHandler {

	List<Star> myStars;
	private String tempVal;
	// to maintain context
	private Star tempStar;
	private int tempIdNum;
	private String tempId;

	public SAXParserStar() {
		myStars = new ArrayList<Star>();
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
			sp.parse("../../stanford-movies/actors63.xml", this);
			//sp.parse("actors63.xml", this);

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
	*/
	private void printData() {

		System.out.println("No of Employees '" + myStars.size() + "'.");
		int counter = 0;
		Iterator<Star> it = myStars.iterator();
		while (it.hasNext() && counter < 1500) {
			counter+=1;
			System.out.println(it.next().toString());
		}
		//System.out.println("num of stars: " + getNumOfStars());
		System.out.println("finish");
	}
	
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
		if (qName.equalsIgnoreCase("actor")) {
			// create a new instance of stars
			tempStar = new Star(); 
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equalsIgnoreCase("actor")) {
			// add it to the list
			tempIdNum += 1;
			tempId = "st" + String.valueOf(tempIdNum);
			tempStar.setId(tempId);
			myStars.add(tempStar);
		} else if (qName.equalsIgnoreCase("stagename")) {
			tempStar.setName(tempVal);
		} else if (qName.equalsIgnoreCase("dob")) {
			tempStar.setBirthYear(checkFilmYear(tempVal));
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
			Statement starStatement = dbcon.createStatement();
			Statement starIdStatement = dbcon.createStatement();
			Statement SIMStatement = dbcon.createStatement();
			 
			String insertStarQuery = "INSERT INTO stars (id, name, birthYear)" +
					   " VALUES(?, ?, ?)"; 
			
			Iterator<Star> it = myStars.iterator();
			while (it.hasNext()) {
				PreparedStatement insertStarStatement = dbcon.prepareStatement(insertStarQuery);
				Star myStar = it.next();
				String starId = myStar.getId();
				if(!checkStarId(starIdStatement,starId)) {
				insertStarStatement.setString(1, starId);
				insertStarStatement.setString(2, myStar.getName());
				insertStarStatement.setInt(3, myStar.getBirthYear());
				insertStarStatement.executeUpdate();
				insertStarStatement.close();
				}
			}
			
			SIMStatement.close();
			starStatement.close();
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
	
	public boolean checkStarId(Statement statement, String starId) {
		boolean starIdExists = false;
		String query = "SELECT id FROM stars WHERE id = \"" + starId + "\"";
		try {
			ResultSet rs = statement.executeQuery(query);
			if(rs.first()) {
				starIdExists = true;
			}
			rs.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:   " + ex.getMessage());
				ex = ex.getNextException();
			}
		}
		return starIdExists;
	}
	
	public static void main(String[] args) {
		SAXParserStar spdf = new SAXParserStar();
		spdf.runExample();
	}
}
