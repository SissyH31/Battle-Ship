import java.awt.*;
import java.util.*;
public class Ship {
	// The x-coordinate of the instance ship(left)
	private int x;
	//The y-coordinate of the instance ship(top)
	private int y;
	// The width of the instance ship
	private int width;
	// The height of the instance ship
	private int height;
	// The corresponding point of the ship (smaller ship more points)
	private int points;
	// The times requires to completely hit down the instance ship
	private int tohitTimes;
	// The times already the ship gets hit  
	private int hitTimes = 0;
	// The shape of the instance ship (the ship itself) -> to be drawn and displayed
	private Rectangle shipShape;
	public void setHitTime () {
		hitTimes++;
	}
	public int getHitTime () {
		return hitTimes;
	}
	public int getX() {
		return x;
	}
	public int getY () {
		return y;
	}
	public void setX(int newX) {
		x = newX;
	}
	public void setY (int newY) {
		y = newY;
	}
	public void setWidth (int w) {
		width = w;
	}
	public void setHeight (int h) {
		height = h;
	}
	public void setshipShapeLocation(int x, int y) {
		this.shipShape.setLocation(x, y);
	}
	public void setshipShape (Rectangle rect) {
		this.shipShape = rect;
	}
	public Rectangle getshipShape () {
		return this.shipShape;
	}
	public int getWidth () {
		return this.width;
	}
	public int getHeight () {
		return this.height;
	}
	public int getPoints () {
		return this.points;
	}
	public int getToHitTimes () {
		return this.tohitTimes;
	}
	// Parameter: the ship "itself", its corresponding points and to hit time to hit down the ship
	// Return: None
	//Constructor: To create a class that stores the ship and related information about the ship (ex.location, rect...)
	public Ship (Rectangle rect, int p, int ht) {
		shipShape = rect;
		x = shipShape.x;
		y = shipShape.y;
		points = p;
		tohitTimes = ht;
		width = shipShape.width;
		height = shipShape.height;
	}
}

