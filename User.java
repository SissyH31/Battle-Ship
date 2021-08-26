// User class: To store the information of a user "account"
public class User implements Comparable <User>{
	// The information of the user includes the user's name, points, besttime record
	private String name;
	private int points;
	private int bestTime;
	public int getTimeSec () {
		return this.bestTime;
	}
	public String getName () {
		return this.name;
	}
	public int getPoints () {
		return this.points;
	}
	public String getTime () {
		return String.format("%02d:%02d", bestTime/60, bestTime%60);
	}
	public void setTime (long time) {
		bestTime = Math.min(bestTime, (int)time);
	}
	public void setPoint (int point) {
		points=Math.max(point, points);
	}
	// Parameter: Name, point, best time
	// Return: None
	// Constructure: To set the name, point, and best time record
	public User (String n, int p, int t) {
		name = n;
		points = p;
		bestTime = t;
	}
	// Parameter: User class to compare
	// Return: : the integer that tells the order the user classes
	// To sort the users based on their points (nature order)
	public int compareTo (User u) {
		return u.points - this.points;
	}
	// Parameter: None
	// Return: The hashcode of the instance class
	// To form a unique hashcode for the user and to make the equals method work
	public int hashCode() {
	    return this.points+bestTime;
	}
	// Parameter: 
	// Return: if the current class(obj) is considered "equal" to the instance class, which is having the same user name (case sensitive)
	// To check if the current class has the same user name as the instance class, which is considered as "equal" and repetition -> renter name
	@Override
	public boolean equals (Object obj) {
		User u = (User) obj;
		return u.name.equals(this.name);
	}
}
