import java.awt.*;
import java.util.*;
public abstract class ComputerAI extends Main {
	// The list that stores the ships with their corresponding info that are placed by the Computer AI
	private static ArrayList<Ship> CAships = new ArrayList<Ship>();
	// The location of the point that hit any ship -> check the sourrounding of this point
	private static int hitx;
	private static int hity;
	// The direction to check by the Computer AI around the point that hits a ship
	private static int direction = 1;
	// If the computer AI guessed right and instead of guessing randomly, it continues to check the places around the hit point
	private static boolean keepHit = false;
	public static ArrayList<Ship> getCAships () {
		return CAships;
	}
	// Parameter: None
	// Return: None
	// If any ship is hit by the AI, add the ship's hit time and if the ship is completely hit down, add the corresponding point to the AI
	public static void addPoint () {
		ships.get(hitShip).setHitTime();
		if (ships.get(hitShip).getHitTime()==ships.get(hitShip).getToHitTimes()) {
			// If the ship is compeletly hit down, there is no need to keep guessing surrounding anymore
			totalPoint+=ComputerAI.getCAships().get(hitShip).getPoints();
			direction = 1;
			keepHit = false;
			hit = false;
		}
	}
	// Parameter: None
	// Return: The point the AI guessed 
	// Once the AI hits any ship, keep guessing the surrounding of the ship
	public static Position guessHitShip () {
		// Keep guessing upwards until it is out of board, taken, or no longer has ship/part of ship hiding in the area
		if (direction == 1) {
			if (hity - squareSize > boardy&&!positions.contains(new Position(hitx, hity - squareSize, Color.white))) {
				keepHit = hitOrNot(hitx, hity - squareSize, Main.ships);
				// If there is ship in the upward direction, keep checking the area
				if (keepHit) {
					addPoint();
					return new Position (hitx, hity - squareSize, Color.red);
				// If there is no longer ship in the upward direction, check the right direction
				} else {
					direction++;
					return new Position (hitx, hity - squareSize, Color.white);
				}
			// If the guess point is out of board, guess the next direction (right)
			} else {
				direction++;
			}
		} 
		// Keep guessing the right direction until it is out of board, taken, or no longer has ship/part of ship hiding in the area
		if (direction==2) {
			if (hitx + squareSize < boardx+squareSize*column&&!positions.contains(new Position(hitx + squareSize, hity, Color.white))) {
				keepHit = hitOrNot(hitx + squareSize, hity, Main.ships);
				if (keepHit) {
					addPoint();
					return new Position (hitx + squareSize, hity, Color.red);
				} else {
					direction++;
					return new Position (hitx + squareSize, hity, Color.white);
				}
			} else {
				direction++;
			}
		} 
		// Keep guessing downwards until it is out of board, taken, or no longer has ship/part of ship hiding in the area
		if (direction==3) {
			if (hity + squareSize < boardy+ squareSize*row&&!positions.contains(new Position(hitx, hity + squareSize, Color.white))) {
				keepHit = hitOrNot(hitx, hity + squareSize, Main.ships);
				if (keepHit) {
					addPoint();
					return new Position (hitx, hity + squareSize , Color.red);
				} else {
					direction++;
					return new Position (hitx, hity + squareSize , Color.white);
				}
			} else {
				direction++;
			}
		} 
		// Keep guessing the left direction until it is out of board, taken, or no longer has ship/part of ship hiding in the area
		if (direction == 4) {
			if (hitx - squareSize > boardx&&!positions.contains(new Position(hitx - squareSize, hity, Color.white))) {
				keepHit = hitOrNot(hitx - squareSize, hity, Main.ships);
				if (keepHit) {
					addPoint();
					return new Position (hitx - squareSize, hity, Color.red);
				} else {
					direction++;
					return new Position (hitx - squareSize, hity, Color.white);
				}
			} else {
				direction++;
			}
		}
		return null;
	}
	// Parameter: None
	// Return: None
	// To clear the placed ships and their information to be ready to start a new turn and place again
	public static void clearCAships () {
		CAships.clear();
	}
	// Parameter: None
	// Return: The position the Computer AI guessed
	// To randomly guess ship positions (Computer AI)
	public static Position guessShip () {
		// To randomly guess a point when no ship is hit yet
		int x = boardx + ((int)(Math.random()*(column))+0)*squareSize + (squareSize/5*2);
		int y = boardy + ((int)(Math.random()*(row))+0)*squareSize + (squareSize/5*2);
		// Keep guessing until a point that is not guessed before
		while (positions.contains(new Position(x, y, Color.white))) {
			x = boardx + ((int)(Math.random()*(column))+0)*squareSize + (squareSize/5*2);
			y = boardy + ((int)(Math.random()*(row))+0)*squareSize + (squareSize/5*2);
		}
		// To check if any ship is hit by the AI, if yes, check the surrounding, no, keep guessing randomly
		hit = hitOrNot(x, y, Main.ships);
		if (hit) {
			addPoint();
			hitx = x;
			hity = y;
			return new Position(x, y, Color.red);
		} else {
			return new Position(x, y, Color.white);
		}
	}
	// Parameter: width of the ship, height of the ship 
	// Return: a rectangle that satisfies the given conditions
	// To place the ships and make sure there is no intersection (computer AI)
	public static Rectangle placeShips (int width, int height) {
		// To randomly choose a place to place the ship -> make sure the ship is completely within the board
		int gridx = (int)(Math.random()*(column))+0;
		int gridy = (int)(Math.random()*(row))+0;
		while (gridx*squareSize+width>squareSize*column) {
			gridx = (int)(Math.random()*(column))+0;
		}
		while (gridy*squareSize+height>squareSize*row) {
			gridy = (int)(Math.random()*(row))+0;
		}
		return new Rectangle (670+boardx+gridx*squareSize, boardy+gridy*squareSize, width, height);
	}
	// Parameter: None
	// Return: None
	// To randomly place the ship(three) to a position that within the board
	public static void placeCAships () {
		CAships.add(new Ship(placeShips(100, 50), 20, 2));
		// check intersectuion
		Rectangle rect2 = placeShips(50, 50);
		// While there is any intersection between the ship, keep randomly placing to a new place
		while (checkIntersection(rect2, 2)) {
			rect2 = placeShips(50, 50);
		}
		CAships.add(new Ship(rect2, 50, 1));
		Rectangle rect3 = placeShips(50, 100);
		while (checkIntersection(rect3, 2)) {
			rect3 = placeShips(50, 100);
		}
		CAships.add(new Ship(rect3, 20, 2));
	}
}
