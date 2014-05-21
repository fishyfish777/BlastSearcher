package blastsearcher;

public class Restful {
	public String postRequest(String query, boolean noUncultured)
	{
		/*
		 * This class posts an HTTP request to NCBI Blast using the specified query
		 * and returns the request ID of the query as a String.
		 * If noUncultured is true, it adds "&EXCLUDE_SEQ_UNCULT=on" to the URL.
		 */
		return "";
	}
	
	public boolean getRequest(String requestID, String filename)
	{
		/*
		 * This class takes a request ID and checks the request at a specific interval until it is ready,
		 * then when ready loads up the results and saves the HTML file of the results with the specified filename
		 */
		
		return false;
	}
}
