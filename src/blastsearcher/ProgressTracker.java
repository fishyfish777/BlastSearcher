package blastsearcher;

public class ProgressTracker {
	public static int counter = 0;
	public static long access = 0;
	public static int totaljobs = 0;

	public static void addToList() {
		counter++;
		totaljobs++;
	}

	public static void completed() {
		counter--;
		if (counter == 0)
		{
			System.out.println("Processing complete!");
			System.exit(0);
		}
	}

	public static int count() {
		return counter;
	}

	public static boolean threeSecondsPassed() {
		if (System.currentTimeMillis() - access <= 3000) {
			return false;
		} else {
			//System.currentTimeMillis() - access > 3000
			access = System.currentTimeMillis();
			return true;
		}
	}
}

