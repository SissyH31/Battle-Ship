import java.util.*;
// This is a Comparator interface class that is used for sorting the user list based on their best time record (Nature order is based on point)
public class sortByBestTime implements Comparator<User> {
	@Override
	// Parameter: User class to compare & another User class to compare 
	// Return: The integer that tells the order the user classes 
	// The implemented compare method that is used to sort users based on their fastest time record 
	public int compare(User u1, User u2) {
		return u1.getTimeSec() - u2.getTimeSec();
	}
}
