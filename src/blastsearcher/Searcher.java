package blastsearcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Searcher extends Thread
{
	public String requestID;
	public String name;
	public String search;
	public int ID;

	public Searcher(String sequenceID, String query, int searcherID)
	{
		/*
		 * Sequence ID is an identifying string, the name of the sequence being searched for
		 * 
		 * The query is the search query that corresponds to the sequence ID
		 * 
		 * The searcherID is the specific ID of the object which is to be called
		 */

		ProgressTracker.addToList();
		name = sequenceID;
		search = query;
		ID = searcherID;
		requestID = this.postRequest(search, MainProgram.exclude_uncultured);
	}

	public void run()
	{
		boolean readyForWriting;
		do
		{
			readyForWriting = this.getRequest(requestID, ID + "");
		} 
		while (!readyForWriting);
		this.write();
	}

	public String postRequest(String query, boolean noUncultured)
	{
		/*
		 * This class posts an HTTP request to NCBI Blast using the specified query and returns the request ID of the query as a String. If noUncultured is true, it adds "&EXCLUDE_SEQ_UNCULT=on" to the URL.
		 */

		// Build the URL
		String url = "http://blast.ncbi.nlm.nih.gov/blast/Blast.cgi?QUERY=" + query + "&DATABASE=nr&NCBI_GI=on&HITLIST_SIZE=3&CMD=Put&PROGRAM=" + MainProgram.program_version + "&EMAIL=tsujinago%40gmail.com";
		String requestID = "";

		if (noUncultured)
		{
			url += "&EXCLUDE_SEQ_UNCULT=on";
		}

		// Download the file temporarily and then parse it
		int i = 0;
		try
		{
			this.downloadHTML("temp", (ID) + " request.html", url);
			requestID = findRequestID("temp", (ID) + " request.html");
			while (requestID.length() <= 3)
			{
				i++;
				System.out.println("RequestID for job " + name + " blank, retrying");
				this.downloadHTML("temp", (ID) + " request.html", url);
				requestID = findRequestID("temp", (ID) + " request.html");
				if (i >= 2)
				{
					break;
				}
			}
			System.out.println(name + " Assigned a request ID of " + requestID);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return the parsed request ID
		return requestID;
	}

	public boolean getRequest(String requestID, String filename)
	{
		/*
		 * This class takes a request ID and checks the request at a specific interval until it is ready,
		 * 
		 * then when ready loads up the results and saves the HTML file of the results with the specified filename,
		 * 
		 * then reports success with a true value and failure with a false value
		 */

		String searchurl = "http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?RID=" + requestID + "&CMD=Get&FORMAT_TYPE=Text";
		int i = 0;
		try
		{
			this.downloadHTML("temp", filename + ".txt", searchurl);
			while (!this.checkIfReady("temp", filename + ".txt"))
			{
				i += 60;
				Thread.sleep(60000);
				this.downloadHTML("temp", filename + ".txt", searchurl);
				System.out.println(ID + " " + name + " is waiting with a request ID of " + requestID + "! (" + i + " seconds)");
			}
			System.out.println(name + " search complete!");
			return true;
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			return false;
		}
	}

	public String findRequestID(String foldername, String filename) throws FileNotFoundException
	{

		/*
		 * Scans a file (namely, the downloaded HTML files) for a specified query.
		 */
		String requestID = "";
		Scanner readFile = new Scanner(new File(foldername + "/" + filename));
		// readFile.useDelimiter("\\s+"); // Delimits @ one or more whitespaces
		while (readFile.hasNextLine())
			if ("<!--QBlastInfoBegin".equalsIgnoreCase(readFile.nextLine()))
			{
				requestID = readFile.nextLine();
				requestID = requestID.substring(10, requestID.length());
				break;
			}
		readFile.close();
		return requestID;
	}

	public boolean checkIfReady(String foldername, String filename) throws FileNotFoundException
	{

		/*
		 * Scans a file (namely, the downloaded HTML files) for a specified query.
		 */
		boolean ready = true;
		Scanner readFile = new Scanner(new File(foldername + "/" + filename));
		while (readFile.hasNextLine())
			if (readFile.nextLine().contains("QBlastInfoBegin"))
			{
				if (readFile.nextLine().contains("Status=WAITING"))
				{
					ready = false;
					break;
				}
			}
		readFile.close();
		return ready;
	}

	public void downloadHTML(String foldername, String filename, String url)
	{
		/*
		 * Downloads the HTML file to the specified folder with the specified filename using the specified url
		 */
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		boolean loop = true;
		while (loop)
		{
			try
			{
				CloseableHttpResponse response = httpclient.execute(httpGet);
				while (!ProgressTracker.threeSecondsPassed())
				{
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				String tempresponse = response.getStatusLine().toString();

				while (!tempresponse.contains("200"))
				{
					while (!ProgressTracker.threeSecondsPassed())
					{
						try
						{
							Thread.sleep(100);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
					System.out.println("Web error - " + response.getStatusLine().toString() + ", retrying");

					response.close();

					httpclient = HttpClients.createDefault();
					httpGet = new HttpGet(url);
					response = httpclient.execute(httpGet);
					tempresponse = response.getStatusLine().toString();
				}

				HttpEntity entity1 = response.getEntity();
				InputStream is = entity1.getContent();

				// Creating appropriate folder
				File dir = new File(foldername);
				dir.mkdir();
				dir = null;

				// Scans the HTML file and writes it sequentially
				String filePath = foldername + "/" + filename;
				FileOutputStream fos = new FileOutputStream(new File(filePath));
				int inByte;
				while ((inByte = is.read()) != -1)
					fos.write(inByte);

				// Closing
				is.close();
				fos.close();
				response.close();
				EntityUtils.consume(entity1);
				loop = false;
			}
			catch (ClientProtocolException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void write()
	{
		String tempbuffer = "";
		String readbuffer;
		String[] tempbufferar;
		String output = "";
		int ctr = 0;
		try
		{
			System.out.println("Writing output for " + name + " with request ID of " + requestID);
			Scanner readFile = new Scanner(new File("temp/" + ID + ".txt"));
			
			System.out.println("ID = " + ID);
			while (readFile.hasNextLine())
			{
				readbuffer = readFile.nextLine();
				if (readbuffer.contains("ALIGNMENTS"))
				{
					// Only filters for one result, for now
					ctr++;
					tempbuffer = readFile.nextLine();
					if (tempbuffer.contains(">"))
					{
						output += ID + "\t";
						output += requestID + "\t";
						output += name + "\t";
						output += search + "\t";
						output += tempbuffer.substring(1) + "\t";
						while (!tempbuffer.contains("Expect = ") && readFile.hasNextLine())
						{
							tempbuffer = readFile.nextLine();
							if (tempbuffer.contains("Expect = "))
							{
								tempbufferar = tempbuffer.split(",");
								tempbufferar = tempbufferar[1].split("Expect = ");
								output += tempbufferar[1] + "\t";
								tempbuffer = readFile.nextLine();
								tempbufferar = tempbuffer.split(",");
								output += tempbufferar[0].substring((tempbufferar[0].indexOf("(") + 1), tempbufferar[0].indexOf(")")) + "\t";
								break;
							}

						}
					}
				}
				else if (readbuffer.contains("Error: CPU usage limit was exceeded"))
				{
					ctr++;
					output = ID + "\t" + requestID + "\t" + name + "\t" + search + "\t" + "NCBI CPU Usage limit exceeded; Task cancelled.";
				}
				else if (readbuffer.contains("No significant similarity found"))
				{
					ctr++;
					output = ID + "\t" + requestID + "\t" + name + "\t" + search + "\t" + "No significant similarity found, see temp file for details.";				}

			}
			int ctr2 = 0;
			if (ctr == 0)
			{
				Scanner readFile3 = new Scanner(new File("temp/" + ID + ".txt"));
				String tempbuffer3;
				while (readFile3.hasNextLine())
				{
					tempbuffer3 = readFile3.nextLine();
					if (tempbuffer3.contains("<li class=\"error\"><p class=\"error\">"))
					{
						output = ID + "\t" + requestID + "\t" + name + "\t" + search + "\t" + tempbuffer3.substring(tempbuffer3.indexOf("<p class=\"error\">") + 17, tempbuffer3.indexOf("</p>"));
						ctr2++;
					}
				}
				if (ctr2 == 0)
				{
					output = ID + "\t" + "\t" + name + "\t" + search + "\t" + "Program Error";
				}
			}
			OutputWriter.printToFile(output);
			readFile.close();
			ProgressTracker.completed();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
