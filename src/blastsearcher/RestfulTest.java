package blastsearcher;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;

/*
 * This class should test being able to interface in REST.
 */

public class RestfulTest {
	
	public static void main(String[] args)
	{
		try {
			/*
			 * This successfully submits a HTTP "Get" request to the server and gets a web page
			 * the contents of which can be ignored except:
			 * 
			 * (Sample Information)
			 * 
			 * <!-QBlastInfoBegin
				RID = 6ZXJ7X1P014
				RTOE = 20
				QBlastInfoEnd 
				->
				
				Also, for a certain RID (say, RS720T7D015) the URL to retrieve the contents is
				http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?RID=RS720T7D015&CMD=Get
				
				Once that URL is retrieved it will have in comment lines:
				<!--
				QBlastInfoBegin
				Status=READY
				QBlastInfoEnd
				-->
				
				Or "Status=WAITING".
				
				If Status is WAITING, wait a bit for the results to pass through.
				
			 */
			
			//Sample query
			System.out
					.print(httpGet("http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?QUERY=TACGGAGGATGCGAGCGTTATCCGGATTTATTGGGTTTAAAGGGTGCGCAGGCGGAAGCGCAAGTCAGCGGTAAAATTGAGAGGCTCAACCTCTTCCCGCCGTTGAAACTGCGTTTCTTGAGTGGGCGAGAAGTACGCGGAATGCGTGGTGTAGCGGTGAAATGCATAGATATCACGCAGAACTCCGATTGCGAAGGCAGCGTACCGGCGCCCAACTGACGCTCATGCACGAAAGCGTGGGTATCGAACAGG&DATABASE=nr&PROGRAM=blastn&FORMAT_TYPE=Text&NCBI_GI=on&HITLIST_SIZE=10&CMD=Put&EXCLUDE_SEQ_UNCULT=on"));
			System.out.print(System.getProperty("user.dir"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String httpGet(String urlStr) throws IOException {
		/*
		 * Code credit to REST tutorial by Dr. M. Elkstein (may replace with Apache's httpget later)
		 */
		  URL url = new URL(urlStr);
		  HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();

		  if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }

		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line + "\n");
		  }
		  rd.close();

		  conn.disconnect();
		  return sb.toString();
		}
	
	
}
