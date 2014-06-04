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

public class Searcher extends Thread {
	public String requestID;
	public String name;
	public String search;
	public int ID;

	public Searcher(String sequenceID, String query, int searcherID) {
		/*
		 * Sequence ID is an identifying string, the name of the sequence being
		 * searched for The query is the search query that corresponds to the
		 * sequence ID The searcherID is the specific ID of the object which is
		 * to be called
		 */

		name = sequenceID;
		search = query;
		ID = searcherID;
		requestID = this.postRequest(search, true);
	}

	public void run()
	{
		System.out.println(name);
		System.out.println(search);
		this.getRequest(requestID, ID + "");
		this.write();
	}
	public String postRequest(String query, boolean noUncultured) {
		/*
		 * This class posts an HTTP request to NCBI Blast using the specified
		 * query and returns the request ID of the query as a String. If
		 * noUncultured is true, it adds "&EXCLUDE_SEQ_UNCULT=on" to the URL.
		 */

		// Build the URL
		String url = "http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?QUERY="
				+ query
				+ "&DATABASE=nr&PROGRAM=blastp&FORMAT_TYPE=Text&NCBI_GI=on&HITLIST_SIZE=10&CMD=Put";
		String requestID = "";

		if (noUncultured) {
			url += "&EXCLUDE_SEQ_UNCULT=on";
		}

		// Download the file temporarily and then parse it
		try {
			this.downloadHTML("temp", (ID) + " request.html", url);
			requestID = findRequestID("temp", (ID) + " request.html");
			System.out.println(name + " Assigned a request ID of " + requestID);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return the parsed request ID
		return requestID;
	}

	public boolean getRequest(String requestID, String filename) {
		/*
		 * This class takes a request ID and checks the request at a specific
		 * interval until it is ready, then when ready loads up the results and
		 * saves the HTML file of the results with the specified filename, then
		 * reports success with a true value (false for a timeout)
		 */

		String searchurl = "http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?RID="
				+ requestID + "&CMD=Get";
		int i = 0;
		try {
			this.downloadHTML("temp", filename + ".html", searchurl);
			while (false == this.checkIfReady("temp", filename + ".html")) {
				i += 60;
				Thread.sleep(60000);
				this.downloadHTML("temp", filename + ".html", searchurl);
				System.out.println(name + " is waiting with a request ID of " + requestID + "! (" + i + " seconds)");
			}
			System.out.println(name + " search complete!");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return true;
	}

	public String findRequestID(String foldername, String filename)
			throws FileNotFoundException {

		/*
		 * Scans a file (namely, the downloaded HTML files) for a specified
		 * query.
		 */
		String requestID = "";
		Scanner readFile = new Scanner(new File(foldername + "/" + filename));
		// readFile.useDelimiter("\\s+"); // Delimits @ one or more whitespaces
		while (readFile.hasNextLine())
			if ("<!--QBlastInfoBegin".equalsIgnoreCase(readFile.nextLine())) {
				requestID = readFile.nextLine();
				requestID = requestID.substring(10, requestID.length());
				break;
			}
		readFile.close();
		return requestID;
	}

	public boolean checkIfReady(String foldername, String filename)
			throws FileNotFoundException {

		/*
		 * Scans a file (namely, the downloaded HTML files) for a specified
		 * query.
		 */
		boolean ready = false;
		Scanner readFile = new Scanner(new File(foldername + "/" + filename));
		// readFile.useDelimiter("\\s+"); // Delimits @ one or more whitespaces
		while (readFile.hasNextLine())
			if ("QBlastInfoBegin".equalsIgnoreCase(readFile.nextLine())) {
				if (readFile.nextLine().contains("READY")) {
					ready = true;
				} else if (readFile.nextLine().contains("WAITING")) {
					ready = false;
				} else {
					System.out.println("Error checking ready status, check "
							+ foldername + "/" + filename);
				}
				break;
			}
		readFile.close();
		return ready;
	}

	public void downloadHTML(String foldername, String filename, String url)
			throws ClientProtocolException, IOException {
		/*
		 * Downloads the HTML file to the specified folder with the specified
		 * filename using the specified url
		 */
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		String tempresponse = "";
		try {
			tempresponse += response.getStatusLine();
			
			while (!tempresponse.contains("200"))
			{
				System.out.println("Web error - trying again in 10 seconds");
				Thread.sleep(10000);
				response = httpclient.execute(httpGet);
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
			EntityUtils.consume(entity1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			response.close();
		}
	}
	
	public void write()
	{
		String output = "";
		String tempbuffer;
		String[] bufferparts;
		//System.out.println("Writing output for " + name);
		Scanner readFile;
		try {
			
			if (new File("temp/" + (ID) + ".html").exists()) {
				output += (name + "\t"
						+ search + "\t");

				readFile = new Scanner(new File("temp/" + (ID)
						+ ".html"));
				while (readFile.hasNextLine()) {
					tempbuffer = readFile.nextLine();
					if (tempbuffer.contains("Select seq ")) {
						bufferparts = tempbuffer.split("Select seq ");
						output += (bufferparts[1].split("<")[0] + "\t");
					}
					if (tempbuffer.contains("Go to alignment for")) {
						bufferparts = tempbuffer.split("]");
						output += (bufferparts[0].substring(29) + "]\t");
						readFile.nextLine();
						readFile.nextLine();
						readFile.nextLine();
						readFile.nextLine();
						readFile.nextLine();
						tempbuffer = readFile.nextLine();
						bufferparts = tempbuffer.split(">");
						output += (bufferparts[1].split("<")[0] + "\t"
								+ bufferparts[3].split("<")[0] + "\n");
						readFile.close();
						System.out.println("Writing " + output);
						Filewriter.printToFile(output);
						break;
					}
				}
			}
			else
			{
				System.out.println("Temporary file does not exist for writing");
			}
			System.out.println("Output written for " + name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
}
