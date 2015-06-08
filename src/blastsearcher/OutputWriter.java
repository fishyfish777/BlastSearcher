package blastsearcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * A static class accessed by the rest of the program, that solves the issue of threads writing to a single file.
 * Accessible also through static methods like Main, if you need to write a message to the output file.
 */
public class OutputWriter
{
	static PrintWriter out;
	
	public static void printToFile(String input)
	{
		try
		{
			out = new PrintWriter(new BufferedWriter(new FileWriter("output.txt", true)));
			System.out.println("Wrote " + input + " to output.txt");
			out.println(input);
			out.flush();
			out.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
