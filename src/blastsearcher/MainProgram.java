package blastsearcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class MainProgram
{
	public static String program_version;
	public static int threads;
	public static int maxRetries;
	public static boolean exclude_uncultured;
	public static boolean cpulimit_error_check;

	public static void main(String[] args)
	{

		/*
		 * Scan a temporary config file. If config file is not found, create and exit
		 */
		Scanner readConfig = null;

		try
		{
			System.out.println("Reading from config.txt");
			readConfig = new Scanner(new File("config.txt"));

			// Variables
			String configbuffer;
			String[] configbufferar;

			// Scan through the config file for relevant values
			while (readConfig.hasNextLine())
			{
				configbuffer = readConfig.nextLine();
				configbufferar = configbuffer.split(":");
				configbufferar[1] = configbufferar[1].replaceAll("\\s+", "");
				if (configbufferar[0].contains("Threads"))
				{
					threads = Integer.parseInt(configbufferar[1]);
				}
				else if (configbufferar[0].contains("Exclude Uncultured") && configbufferar[1].toLowerCase().contains("y"))
				{
					exclude_uncultured = true;
				}
				else if (configbufferar[0].contains("Exclude Uncultured") && configbufferar[1].toLowerCase().contains("n"))
				{
					exclude_uncultured = false;
				}
				else if (configbufferar[0].contains("Blast Version"))
				{
					program_version = configbufferar[1].toLowerCase();
				}
				else if (configbufferar[0].contains("Maximum amount of times"))
				{
					try
					{
						maxRetries = Integer.parseInt(configbufferar[1]);
					}
					catch (NumberFormatException e)
					{
						System.out.println("Error in config file: Max amount of resets not an integer value.");
					}
				}
				else if (configbufferar[0].contains("CPU Limit Exceeded") && configbufferar[1].toLowerCase().contains("y"))
				{
					cpulimit_error_check = true;
				}
				else if (configbufferar[0].contains("CPU Limit Exceeded") && configbufferar[1].toLowerCase().contains("n"))
				{
					cpulimit_error_check = false;
				}
				else
				{
					System.out.println("Error in config.txt!");
					throw new FileNotFoundException();
				}
			}
			System.out.println("Initialized with:");
			System.out.println("Threads: \"" + MainProgram.threads + "\"");
			System.out.println("Program Version: \"" + MainProgram.program_version + "\"");
			System.out.println("Exclude Uncultured: \"" + MainProgram.exclude_uncultured + "\"");
		}
		/* Write a config file if the config file is not found. */
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.out.println("Invalid config.txt, generating default and exiting:");
			PrintWriter configout;
			try
			{
				configout = new PrintWriter(new BufferedWriter(new FileWriter("config.txt")));
				configout.println("Threads (adding more threads may not make program faster, max 100): 5");
				configout.println("Exclude Uncultured (y/n): y");
				configout.println("Blast Version (blastn/blastp/blastx/tblastn/tblastx, only blastn and blastp tested): blastp");
				configout.println("Maximum amount of times a failed request should be submitted (0 to 2 billion): 3");
				configout.println("Only process \"CPU Limit Exceeded\" Errors (y/n) (currently nonfunctional, leave n): n");
				configout.flush();
				configout.close();
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.exit(0);
		}
		finally
		{
			readConfig.close();
		}
		if (!cpulimit_error_check)
		{
			/*
			 * Scan through the fasta file and create Searcher objects at 3-second intervals
			 */
			deleteFolder(new File("temp/"));
			JFileChooser fileChooser = new JFileChooser();
			File currentDir = new File(System.getProperty("user.dir"));
			fileChooser.setCurrentDirectory(currentDir);
			int returnValue = fileChooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION)
			{
				// Making sure the right file is selected
				File selectedFile = fileChooser.getSelectedFile();
				System.out.println("Reading " + selectedFile.getName());
				Scanner readFile;
				Scanner readFile2;
				String tempbuffer = "";
				String tempbuffer2 = "";
				String tempSequenceID = "";
				String tempquery = "";
				List<Searcher> searchers = new ArrayList<Searcher>();
				List<Thread> threads = new ArrayList<Thread>();
				int searcherID = 0;
				try {
					readFile = new Scanner(selectedFile);
					readFile2 = new Scanner(selectedFile);
					
					if (readFile2.hasNextLine())
					{
						tempbuffer2 = readFile2.nextLine();
						while (tempbuffer2.equals("") && readFile2.hasNextLine())
						{
							tempbuffer = readFile.nextLine();
							tempbuffer2 = readFile2.nextLine();
						}
						if (tempbuffer2.contains(">"))
						{
							tempSequenceID = tempbuffer2.substring(1);
						}
					}
					else
					{
						//Error: File unreadable
					}
					while (readFile2.hasNextLine()) 
					{
						tempbuffer = readFile.nextLine();
						tempbuffer2 = readFile2.nextLine();
						if (tempbuffer2.equals(""))
						{
							// Skipping blank line
						}
						else if (tempbuffer2.contains(">")) 
						{
							searcherID++;
							// Line 2 contains ">", so
							// At this point, the query, sequence ID and ID are built
							// This means that tempSequenceID is set first, then added, then acquired anew through tempbuffer 2
							// Tempquery would just be added and ended, so
							// This completes the query.

							tempquery += tempbuffer;
							searchers.add(new Searcher(tempSequenceID, tempquery,searcherID));
							threads.add(searchers.get(searcherID - 1));
							Thread.sleep(3000);
							threads.get(searcherID - 1).start();
							tempSequenceID = tempbuffer2.substring(1);
							tempquery = "";
							while (ProgressTracker.count() >= MainProgram.threads) 
							{
								System.out.println("Thread limit reached, waiting for current threads...");
								Thread.sleep(60000);
							}
						}
						else if (!tempbuffer.contains(">") && readFile2.hasNextLine())
						{
							//In this condition, the next line is NOT a ">"
							//Because the program has already advanced one line, tempbuffer would be added to tempquery.
							tempquery += tempbuffer;
						}
					}
					if (!readFile2.hasNextLine() && readFile.hasNextLine())
					{
						// tempbuffer2 would be the last line, therefore it is added to the query
						searcherID++;
						tempquery += tempbuffer2;
						searchers.add(new Searcher(tempSequenceID, tempquery,searcherID));
						threads.add(searchers.get(searcherID - 1));
						Thread.sleep(3000);
						threads.get(searcherID - 1).start();
						while (ProgressTracker.count() >= MainProgram.threads) 
						{
							System.out.println("Thread limit reached, waiting for current threads...");
							Thread.sleep(60000);
						}
					}
					if (tempbuffer.equals(""))
					{
						//file not readable
					}
				}
				catch (FileNotFoundException e)
				{
					// File not found or accessible at scanner initialization
					System.out.println("Error: Input file not found or accessible to program, check permissions. Exiting.");
					OutputWriter.printToFile("Error: Input file not found or accessible to program, check permissions.");
					System.exit(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			MainProgram.endOfProgram();
		}

	}

	public static void deleteFolder(File folder)
	{
		// Shamefully used this off StackExchange. It works.

		File[] files = folder.listFiles();
		if (files != null)
		{
			for (File f : files)
			{
				if (f.isDirectory())
				{
					deleteFolder(f);
				}
				else
				{
					f.delete();
				}
			}
		}
		folder.delete();
	}

	public static void endOfProgram()
	{
		/*
		 * Currently unimplemented function, should not affect program.
		 */
		OutputWriter.printToFile("Total jobs = " + ProgressTracker.totaljobs);
		File selectedFile = new File("output.txt");
		System.out.println("Reading " + selectedFile.getName());
		Scanner readFile;
		String tempbuffer = "";
		new ArrayList<Searcher>();
		new ArrayList<Thread>();
		try
		{
			tempbuffer = "";
			readFile = new Scanner(selectedFile);

			while (readFile.hasNextLine())
			{
				tempbuffer = readFile.nextLine();
				if (tempbuffer.contains("NCBI CPU Usage limit exceeded"))
				{
					tempbuffer.split("\t");
					System.out.println("CPU usage limit exceeded error found, please retry queries");
					OutputWriter.printToFile("CPU usage limit exceeded error found, please retry queries");
				}
			}

			// Create threads
			readFile.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Error reading input file while checking for CPU Usage Limit exceeded errors. Check permissions?");
			OutputWriter.printToFile("Error reading input file while checking for CPU Usage Limit exceeded errors. Check permissions?");
			e.printStackTrace();
		}
	}

}
