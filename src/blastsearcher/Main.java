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

public class Main {
	public static String program_version;
	public static int threads;
	public static boolean exclude_uncultured;

	public static void main(String[] args) {
		/*
		 * Scan a temporary config file If config file not found, create and
		 * exit
		 */
		try {
			System.out.println("Reading from config.txt");
			Scanner readConfig = new Scanner(new File("config.txt"));
			String configbuffer; String[] configbufferar;
			while (readConfig.hasNextLine())
			{
				configbuffer = readConfig.nextLine();
				configbufferar = configbuffer.split(":");
				configbufferar[1] = configbufferar[1].replaceAll("\\s+","");
				if (configbufferar[0].contains("Threads"))
				{
					threads = Integer.parseInt(configbufferar[1]);
				}
				else if (configbufferar[0].contains("Exclude Uncultured"))
				{
					if (configbufferar[1].toLowerCase().contains("y"))
					{
						exclude_uncultured = true;
					}
					else if (configbufferar[1].toLowerCase().contains("n"))
					{
						exclude_uncultured = false;
					}
				}
				else if (configbufferar[0].contains("Blast Version"))
				{
					program_version = configbufferar[1].toLowerCase();
				}
				else
				{
					System.out.println("Error in config.txt!");
					throw new FileNotFoundException();
				}
			}
			System.out.println("Initialized with:");
			System.out.println("Threads: \"" + Main.threads +  "\"");
			System.out.println("Program Version: \"" + Main.program_version +  "\"");
			System.out.println("Exclude Uncultured: \"" + Main.exclude_uncultured +  "\"");
			readConfig.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Invalid config.txt, generating default and exiting:");
			PrintWriter configout;
			try {
				configout = new PrintWriter(new BufferedWriter(new FileWriter(
						"config.txt")));
				configout.println("Threads (adding more threads may not make program faster, max 100): 20");
				configout.println("Exclude Uncultured (y/n): y");
				configout.println("Blast Version (blastn/blastp/blastx/tblastn/tblastx, only blastn and blastp tested): blastp");
				configout.flush();
				configout.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.exit(0);
		}

		/*
		 * Scan through the fasta file and create Searcher objects at 3-second
		 * intervals
		 */
		deleteFolder(new File("temp/"));
		JFileChooser fileChooser = new JFileChooser();
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			System.out.println("Reading " + selectedFile.getName());
			Scanner readFile;
			String tempbuffer;
			String tempSequenceID;
			String tempquery;
			List<Searcher> searchers = new ArrayList<Searcher>();
			List<Thread> threads = new ArrayList<Thread>();
			int searcherID = 0;
			try {
				readFile = new Scanner(selectedFile);
				while (readFile.hasNextLine()) {
					// Read in Sequence ID, Build query, flush to object, repeat
					tempbuffer = readFile.nextLine();

					if (tempbuffer.contains(">")) {
						searcherID++;
						tempquery = "";
						tempSequenceID = tempbuffer.substring(14);
						if (readFile.hasNextLine()) {
							tempbuffer = readFile.nextLine();
						}
						while (!tempbuffer.contains("*")) {
							tempquery += tempbuffer;
							if (readFile.hasNextLine()) {
								tempbuffer = readFile.nextLine();
							} else {
								break;
							}
						}
						if (tempbuffer.contains("*")) {
							tempquery += tempbuffer;
						}
						// At this point, the query, sequence ID and ID are
						// built
						searchers.add(new Searcher(tempSequenceID, tempquery,
								searcherID));
						threads.add(searchers.get(searcherID - 1));
						Thread.sleep(3000);
						threads.get(searcherID - 1).start();
						while (ProgressTracker.count() >= Main.threads) {
							System.out
									.println("Thread limit reached, waiting for current threads...");
							Thread.sleep(60000);
						}
					}
				}
				// Create threads
				readFile.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

}
