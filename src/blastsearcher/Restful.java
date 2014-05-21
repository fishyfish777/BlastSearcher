package blastsearcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Restful {

	public static String postRequest(String query, boolean noUncultured) {
		/*
		 * This class posts an HTTP request to NCBI Blast using the specified
		 * query and returns the request ID of the query as a String. If
		 * noUncultured is true, it adds "&EXCLUDE_SEQ_UNCULT=on" to the URL.
		 */

		// Build the URL
		String url = "http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?QUERY="
				+ query
				+ "&DATABASE=nr&PROGRAM=blastn&FORMAT_TYPE=Text&NCBI_GI=on&HITLIST_SIZE=10&CMD=Put";
		if (noUncultured) {
			url += "&EXCLUDE_SEQ_UNCULT=on";
		}

		try {
			downloadHTML("test folder", "asdf.html", url);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public boolean getRequest(String requestID, String filename) {
		/*
		 * This class takes a request ID and checks the request at a specific
		 * interval until it is ready, then when ready loads up the results and
		 * saves the HTML file of the results with the specified filename
		 */

		return false;
	}

	public static void downloadHTML(String foldername, String filename,
			String url) throws ClientProtocolException, IOException {
		/*
		 * Downloads the HTML file to the specified folder with the specified
		 * filename using the specified url
		 */
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpGet);

		try {
			System.out.println(response.getStatusLine());
			HttpEntity entity1 = response.getEntity();
			InputStream is = entity1.getContent();

			// Creating appropriate folder
			File dir = new File(foldername);
			dir.mkdir();
			dir = null;

			String filePath = foldername + "/" + filename;
			FileOutputStream fos = new FileOutputStream(new File(filePath));

			// Scans the HTML file and writes it
			int inByte;
			while ((inByte = is.read()) != -1)
				fos.write(inByte);

			// Closing
			is.close();
			fos.close();
			EntityUtils.consume(entity1);
		} finally {
			response.close();
		}

	}
}
