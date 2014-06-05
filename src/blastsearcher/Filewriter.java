package blastsearcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Filewriter {
	static PrintWriter out;
	public static void printToFile(String input)
	{	
		
		//System.out.println("In method printToFile, writing" + input);
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(
					"output.txt", true)));
			System.out.println("Wrote " + input + " to output.txt");
			out.println(input);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Output to ProgressTracker which ID has been written so that another ID can start
	}
}
