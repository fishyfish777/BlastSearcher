package blastsearcher;

public class ProgressTracker
{
	public static int counter = 0;
	public static long access = 0;
	public static int totaljobs = 0;

	public static void addToList()
	{
		counter++;
		totaljobs++;
	}

	public static void completed()
	{
		counter--;
		if (counter == 0)
		{
			System.out.println("Processing complete! Checking for CPU Exceeded errors...");
			MainProgram.endOfProgram();
		}
	}

	public static int count()
	{
		return counter;
	}

	/*
	 * A static class accessed by a bunch of looped threads which tells them when it's okay to proceed.
	 * This 3 second limitation is due to NCBI's usage guidelines which state that the server is to be polled no more often than once every 3 seconds.
	 */
	public static boolean threeSecondsPassed()
	{
		if (System.currentTimeMillis() - access <= 3000)
		{
			return false;
		}
		else
		{
			// System.currentTimeMillis() - access > 3000
			access = System.currentTimeMillis();
			return true;
		}
	}
}
