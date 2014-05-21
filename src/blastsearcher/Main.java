package blastsearcher;

public class Main {
	public static void main(String[] args)
	{
		/*
		 * For now until a GUI is up and running the main class will just read input from blastinput.txt
		 * 
		 * blastinput.txt's file format (copy-pasted from the Excel file) is:
		 * --------- Sequence ID ------------------- Tab ------------------ Sequence --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		 * M02127_12_000000000-A6WTY_1_1101_14197_4047	TACGGAGGATGCGAGCGTTATCCGGATTTATTGGGTTTAAAGGGTGCGTAGGCGGGTTATCAAGTCAGCGGTAAAATCGTGGAGCTCAACTCCATCCAGCCGTTGAAACTGATGATCTTGAGTGGGCGAGAAGTATGCGGAATGCGTGGTGTAGCGGTGAAATGCATAGATATCACGCAGAACTCCGATTGCGAAGGCAGCATACCGGCGCCCGACTGACGCTGAAGCACGAAAGCGTGGGTATCGAACAGG
		 * M02127_12_000000000-A6WTY_1_1101_24811_7295	TACGTAGGGGGCAAGCGTTATCCGGATTTACTGGGTGTAAAGGGAGCGTAGACGGCAGCGCAAGTCTGAAGTGAAATGCCGGGGCTTAACCCCGGAACTGCTTTGGAAACTGTGCAGCTAGAGTGCAGGAGAGGTAAGTGGAATTCCTAGTGTAGCGGTGAAATGCGTAGATATTAGGAGGAACACCAGTGGCGAAGGCGGCTTACTGGACTGTAACTGACGTTGAGGCTCGAAAGCGTGGGGAGCAAACAGG
		 * 
		 * What the program will do is:
		 * 1. Search the sequence in NCBI Blast
		 * 2. Wait for the search to complete
		 * 3. Save the resulting HTML page in a folder as "[insert Sequence ID here].html"
		 * 4. Continue onto the next sequence
		 * 
		 * This will be kind of slow considering that each search takes up to a minute but it is okay because it is automated.
		 * 
		 * 	
		 */
		try {
			Restful.postRequest("TACGGAGGATGCGAGCGTTATCCGGATTTATTGGGTTTAAAGGGTGCGCAGGCGGAAGCGCAAGTCAGCGGTAAAATTGAGAGGCTCAACCTCTTCCCGCCGTTGAAACTGCGTTTCTTGAGTGGGCGAGAAGTACGCGGAATGCGTGGTGTAGCGGTGAAATGCATAGATATCACGCAGAACTCCGATTGCGAAGGCAGCGTACCGGCGCCCAACTGACGCTCATGCACGAAAGCGTGGGTATCGAACAGG",true);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
