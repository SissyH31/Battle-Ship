import java.awt.Color;
import java.util.*;
public class Position {
	// The x-coordinate of the point
	private int x;
	// The y-coordinate of the point
	private int y;
	// The corresponding color of the point (red - hit/white - not hit)
	private Color color;
	public Color getColor() {
		return color;
	}
	public int getX () {
		return this.x;
	}
	public int getY () {
		return this.y;
	}
	// Parameter: The x-coordinated of the displayed hitting point, The y-coordinate of the displayed hitting point, the color of the point
	// Return: None
	// Constructor: To a new Position class that records the location of the hitting point and its display color(hit – red; empty – white)
	public Position(int x, int y, Color c) {
		this.x = x;
		this.y = y;
		this.color = c;
	}
	// Parameter: None
	// Return: The hashcode of theinstance class
	// To form the unique hashcode for the points and to make the equals method work
	@Override
	public int hashCode() {
	    return this.x + this.y;
	}
	// Parameter: the object to compare with
	// Return: If the class to compare is considered as “equal’ to the current class
	// To make sure there is no same positions that is already hit before gets added to the hashset of Position classes and displayed again
	@Override
	public boolean equals (Object obj){
		Position p = (Position) obj;
		return (this.x == p.x && this.y == p.y);
	}
}
