package blastsearcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class Main {
	public static void main(String[] args) {
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
						//At this point, the query, sequence ID and ID are built
						searchers.add(new Searcher(tempSequenceID, tempquery,
								searcherID));
						threads.add(searchers.get(searcherID-1));
						Thread.sleep(3000);
						threads.get(searcherID-1).start();
						while (ProgressTracker.count() >= 50)
						{
							System.out.println("Thread limit reached, waiting for current threads...");
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
