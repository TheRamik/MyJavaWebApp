import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class GetTimeResults {
	public static Scanner scanner = new Scanner(System.in);
	public static int numOfLines;
	public static long totalTS, totalTJ;
	public static long avgTSInMS, avgTJInMS;
	
	public GetTimeResults () {
		numOfLines = 0;
		totalTS = 0;
		totalTJ = 0;
		avgTSInMS = 0;
		avgTJInMS = 0;
	}
	
	public static String getTotalFromStr (String total) {
		String[] Total = total.split(" = ");
		return Total[1];
	}
	
	public static void readLine(String line) {
		String[] splitLine = line.split(", ");
		String TS = getTotalFromStr(splitLine[0]);
		String TJ = getTotalFromStr(splitLine[1]);
		long longTS = Long.parseLong(TS);
		long longTJ = Long.parseLong(TJ);
		totalTS += longTS;
		totalTJ += longTJ;
	}
	
	public static void readFile(String input) {
		try (Stream<String> lines = Files.lines(Paths.get(input))) {
	        for( String line : (Iterable<String>) lines::iterator) {
	        	numOfLines++;
	        	readLine(line);
	        }
	        if (numOfLines != 0 ) {
	        	long avgTS = totalTS/numOfLines;
	        	long avgTJ = totalTJ/numOfLines;
	        	avgTSInMS = TimeUnit.NANOSECONDS.toMillis(avgTS);
	        	avgTJInMS = TimeUnit.NANOSECONDS.toMillis(avgTJ); 
	        }
	        	
		}
		catch (IOException ex) {
			System.out.println("Error in reading file");
			return;
		}
	}
	
	public static void main (String[] args)
	{
		System.out.println("Type in the name of og files of the file for the SearchLog: ");
		String input = scanner.nextLine();
		System.out.println( "Your input = " + input );
		
		readFile(input);
		System.out.println("The Average Search Servlet Time in milliseconds: " + avgTSInMS);
		System.out.println("The Average JDBC Time in milliseconds: " + avgTJInMS);
	}
	
}