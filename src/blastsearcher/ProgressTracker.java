package blastsearcher;

import java.util.ArrayList;
import java.util.List;

public class ProgressTracker {
	public static int counter = 0;
	public static void addToList()
	{
		counter++;
	}
	public static void completed()
	{
		counter--;
	}
	public static int count()
	{
		return counter;
	}

}
