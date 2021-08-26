import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.sound.sampled.*;
import javax.swing.*;
// Main class: To build up all the panels and keep track of the move of the user (ex. mouse move to place ship) 
public class Main extends JPanel implements MouseMotionListener, MouseListener{
	// The frame 
	static JFrame frame;
	// The size of the little grids
	static int squareSize = 50;
	// The radius of the display hitting point
	private int radius = 10;
	// The number of rows & columns of the big grid(board)
	protected static int row = 5;
	protected static int column = 5;
	// The location of the board (left one)
	protected static int boardx = 200;
	protected static int boardy = 105;
	// The difference between the location of the point the mouse is dragging and the location of the dragged ship(leftup)
	// For moving the ship with the mouse
	private int prex, prey;
	private static int shipNum = 0;
	// The lated ship that the user moved - for rotation - defalut to rotate the latest moved ship (default set as ship 1)
	private static int lastShipNum = 0;
	// The index of the ship that is hit by the Computer AI
	protected static int hitShip = 0;
	// The x and y of the clicked point by the user to hit ship
	private int pointX, pointY;
	// The x-coordinate of the ship when the user is dragging it(left)
	// The y-coordinate of the ship when the user is dragging it(top)
	private int shipx, shipy;
	// The number that keep track of which stage the game is on and display different panels based on this number 
	// 0 - start, 1 - place, 2 - battle, 3 - user, 4 - rank;
	private int playMode = 0;
	// The point of the Computer AI in the current turn
	protected static int totalPoint = 0;
	// // The point of the user in the current turn
	private int CAtotalPoint = 0;
	// The time that passed
	private int second = 0;
	// To show the winning windiow
	private int win;
	// To ask the user if to change to anouther user "account"
	private int changeUser;
	// If the computer AI hits any ship
	protected static boolean hit = false;
	// If the user hits any ship
	private boolean CAhit = false;
	// If it is the user's turn
	private boolean userTurn = true;
	// To initalize the place panel with ships
	private boolean initialize = true;
	// To set the initialize of the battle field - do not start until the user clicked a valid point
	private boolean initialize2 = true;
	// If the mouse is keep pressing and dragging the ship
	private boolean press = false;
	// if the mouse clicked a valid point (no repeat, )
	private boolean click = false;
	// If to turn on the bgm or not
	private boolean bgm = true;
	// The user can not change level set after the actual game start(battle panel)
	private boolean levelLimit = false;
	//  The location & color of the clicked point (bith for the user and the AI)
	private Position point;
	// The current player of the game - for user "account"
	private String currentUser;
	// To move the ship while the user is dragging it
	private Rectangle updateRect;
	// The list that stores all the ship class of the user
	protected static ArrayList<Ship> ships = new ArrayList<Ship>();
	// The list to be sorted based on either points or time -> for sorting and displaying the ranking
	private ArrayList<User> sortForRank;
	// start1: The initial start button at the beginning
	// start2: The start button when the user finishs placing the ships and is ready for the actual game
	// finish: The finish button when the user finished entering the user name
	// next: The button for transferring from the ranking by point page to ranking by time page
	// rotation: The button to let the user to rotate the ship
	// sound: The button to allow the user turn on/off the BGM
	// exit: The button for exiting
	// restart: The button to allow the user to restart the game and play again
	private JButton start1, start2, rotation, finish, next, sound, exit, restart;
	// For entering the username
	private JTextField name;
	// userName: To show the title “username” for the user panel
	// tp: The point section that is shown in the user panel part (computer AI’s points)
	// CAtp: The point section that is shown in the Computer AI panel part (the user’s points)
	// time: The time section that shows the time passed during the battle section
	// timeTitle: The title “Time” for the battle field panel
	// rankTitle: The title of the raking (points/time)
	// empty - remind the user the name can not be empty; taken - to remind the user the name is taken; tooLong - to remind the user the name is too long
	private JLabel userName, empty, taken, tooLong, tp, CAtp, time, timeTitle, rankTitle;
	// The list that stores all the positions that are already hit by the user/computer(also for displaying the red/white points)
	protected static HashSet<Position> positions = new HashSet<Position>();
	// String that records the username for easy searching and accessing, and the user class stores related information of the user (when update point/time of a user, search it here and change/set)
	private HashMap<String, User> users = new HashMap<String, User>();
	// Background images for different panels
	private Image ocean, cover, user, ranking;
	// The BGM of the game
	private Clip BGM;
	// The thread that keep track of the time(timer role) after the battle section starts
	private Thread thread;
	// Parameter: -	The x-coordinate of the mouse-click point, the y-coordinate of the mouse-click point, the ship class list to check 
	// Return: If any ship in the given Ship list is hit
	// To check if any ship in the given Ship list is hit by the current chosen hitting point
	public static boolean hitOrNot (int x, int y, ArrayList<Ship> ship) {
		// To check the ships one by one if the user's clicked point is inside the ship - hit the ship
		for (int i = 0; i < ship.size(); i++) {
			if (ship.get(i).getshipShape().contains(x, y)) {
				// Record the index of the ship that is hit
				hitShip = i;
				return true;
			}
		}
		return false;
	}
	// Parameter: : The mouse event of where the user pressed the mouse on the panel
	// Return: The index of the ship the mouse click
	// To find which ship does the user choose to move
	public int shipNumber (MouseEvent e) {
		// To check the ships one by one and figure out which ship is the user trying to drag
		for (int i = 0; i < ships.size(); i++) {
			if (ships.get(i).getshipShape().contains(e.getX(), e.getY())) {
				// Return the index of the dragging ship, if none is being dragged, return -1
				return i;
			}
		}
		return -1;
	}
	// Parameter: None
	// Return: None
	// To update the point sections on the battle panel
	public void updatePoint () {
		CAtp.setText("Your Points   " + CAtotalPoint);
		tp.setText("Computer Points   " + totalPoint);
	}
	// Parameter: None
	// Return: None
	// To check and limits the ship to only be in the board when the user drags it
	public void checkShip () {
		// To check if the ship is moving to "left" or "right"
		// If it is, then just place it at the board line - no over it
		if (shipx < boardx) {
			shipx = boardx;
		}
		if (shipx > boardx+squareSize*column-ships.get(shipNum).getWidth()) {
			shipx = boardx+squareSize*column-ships.get(shipNum).getWidth();
		}
		// To check if the ship is moving to "up" or "down"
		// If it is, then just place it at the board line - no over it
		if (shipy < boardy) {
			shipy = boardy;
		}
		if (shipy>boardy+squareSize*row-ships.get(shipNum).getHeight()) {
			shipy = boardy+squareSize*row-ships.get(shipNum).getHeight();
		}
	}
	// Parameter: the ship(rect) to check, and which list to check(mode 1-user's ships; mode 2-AI's ships)
	// Return: If there is a intersection between the ships
	// To check if there is intersection between the ships of the specific ship list
	public static boolean checkIntersection (Rectangle rect, int mode) {
		// The list to check
		ArrayList<Ship> list;
		// The index of the ship to check
		int sn = -1;
		// Change the ship list to check based on the "mode"
		// mode 1 - user placing ship, mode 2 - AI placing ship, mode 3 - rotation 
		// 1 - check the index of the current ship(shuipNum), 3 - check the index of thelast moved ship  
		if (mode==1||mode==3) {
			list = new ArrayList<Ship>(ships);
			if (mode==1) {
				sn = shipNum;
 			} else {
 				sn = lastShipNum;
 			}
		} else {
			list = new ArrayList<Ship>(ComputerAI.getCAships());
		}
		// To check if the given ship intersects any ship within the list (not including itself)
		for (int i = 0; i < list.size(); i++) {
			if (rect.intersects(list.get(i).getshipShape())&&i!=sn) {
				return true;
			}
		}
		return false;
	}
	// Parameter: None
	// Return: None
	// To check and correct the ship to be on/align the line of the grids and 
	// to not intersect other ships when the user releases the mouse
	public void releaseCheck() {
		// If the ship is not on lines, move it to the cloest line (left/right -> depends on closer to which way)
		if ((shipx-boardx)%squareSize!=0) {
			if ((shipx-boardx)%squareSize<squareSize/2) {
				shipx = boardx+(shipx-boardx)/squareSize*squareSize;
			} else {
				shipx = boardx+((shipx-boardx)/squareSize+1)*squareSize;
			}
		}
		if ((shipy-boardy)%squareSize!=0) {
			if ((shipy-boardy)%squareSize<squareSize/2) {
				shipy = boardy+(shipy-boardy)/squareSize*squareSize;
			} else {
				shipy = boardy+((shipy-boardy)/squareSize+1)*squareSize;
			}
		}
		// If the ship intersects with any other ships, move it back to where it was (original x and y)
		if (checkIntersection(ships.get(shipNum).getshipShape(), 1)) {
			shipx = ships.get(shipNum).getX();
			shipy = ships.get(shipNum).getY();
		} else {
			// If the ship's location is valid, update the information of the ship 
			ships.get(shipNum).setshipShape(updateRect);
			ships.get(shipNum).setX(shipx);
			ships.get(shipNum).setY(shipy);
		}
	}
	// Parameter: None
	// Return: None
	// To update the point sections in the battleship panel
	public void updateShip (MouseEvent e) {
		// To update the newest location of the dragging ship
		shipx = e.getX()+prex;
		shipy = e.getY()+prey;
		// To check if the ship is within the board
		checkShip();
		// To set the location of the moving ship(updateRect)
		updateRect.setLocation(shipx, shipy);
		// If the user is no longer dragging the ship, checking if there is intersection/on line
		if (!press) {
			releaseCheck();
		}
		// Set the location of the ship to the newest updated location
		ships.get(shipNum).setshipShapeLocation(shipx, shipy);
		repaint();
	}
	// Parameter: None
	// Return: If the name the user entered is valid
	// To check if the name the user entered is valid and shows texts to remind the user why the name is invalid
	public boolean checkUserName () {
		// To check and remind the user if the name is empty
		if (name.getText().isEmpty()) {
			add(empty);
			repaint();
			return false;
		} 
		// To check and remind the user if the name is already taken
		if (users.containsKey(name.getText())) {
			add(taken);
			repaint();
			return false;
		}
		// To check if the name is too long (more than 20 characters) and remind the user
		if (name.getText().length()>=15) {
			add(tooLong);
			repaint();
			return false;
		}
		// Store the data later to the current user class
		currentUser = name.getText();
		// To add the new user to the user class list
		users.put(name.getText(), new User (name.getText(), 0, 600));
		return true;
	}
	// Parameter: None
	// Return: None
	// To set up the panel that asks the user to enter a name
	public void setUserPanel () {
		// To set the panel for the user to enter username
		removeAll();
		add(sound);
		// To add the title user name & the text box for entering name
		userName = new JLabel("User Name");
		userName.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
		userName.setLocation(275, 100);
		userName.setSize(200, 100);
		name = new JTextField();
		name.setLocation(185, 200);
		name.setSize(300, 50);
		// To add the finish button for continuing
		finish = new JButton ("finish");
		finish.setLocation(285, 300);
		finish.setSize(100, 50);
		// To add the reminds if the name entered is not valid
		empty = new JLabel("The name can not be empty");
		taken = new JLabel("The name is already taken, please enter again");
		tooLong = new JLabel("This name is too long");
		empty.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
		empty.setSize(700, 50);
		empty.setLocation(185, 155);
		tooLong.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
		tooLong.setSize(700, 50);
		tooLong.setLocation(185, 155);
		taken.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
		taken.setSize(700, 50);
		taken.setLocation(130, 155);
		// If finish is clicked, check if the name entered is valid 
		// invalid - reminds and enter again; valid - continue to the place ship panel
		finish.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				remove(tooLong);
				remove(empty);
				remove(taken);
				if (checkUserName ()) {
					setPlacePanel();
				} 
			}
		});	
		// Add everything to the panel and set the playmode to 3 (user mode)
		add(finish);
		add(userName);
		add(name);
		playMode = 3;
		repaint();
	}
	// Parameter: None
	// Return: None
	// To set up the panel that allows the user to place the ships to certain position
	public void setPlacePanel () {
		// To remove all the previous and add the new ones
		// Including: sound/bgm button, start for continuing to the battle panel, rotation button
		removeAll();
		add(sound);
		start2 = new JButton("Start");
		start2.setLocation(270, 2);
		start2.setSize(100, 30);
		// Once start clicked, continue to the battle stage
		start2.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				setBattlePanel ();
			}
		});
		rotation = new JButton("Rotation");
		rotation.setLocation(270, 405);
		rotation.setSize(100, 30);
		// Once rotation clicked, rotate the LATEST MOVED ship
		// Check and rotate when there would be no out of bard and intersection after rotation
		rotation.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				Ship s = ships.get(lastShipNum);
				if (s.getX()+s.getHeight()<=boardx+squareSize*column&&s.getY()+ s.getWidth()<=boardy+squareSize*row&&!checkIntersection(ships.get(lastShipNum).getshipShape(), 3)) {
					// Update ship to the rotated one (change width and height)
					s.setshipShape(new Rectangle(s.getX(), s.getY(), s.getHeight(), s.getWidth()));
					int h = s.getHeight();
					s.setHeight(s.getWidth());
					s.setWidth(h);
					repaint();
				} 
			}
		});
		// add componenets and update playmode to 1 (place ship stage)
		add(start2);
		add(rotation);
		playMode = 1;
		repaint();
	}
	// Parameter: None
	// Return: None
	// To set up the panel where the user and the computer AI battle
	public void setBattlePanel () {
		// remove start and rotation button, add battle stage components
		remove(start2);
		remove(rotation);
		// Since the frame size changes, the location of buttons(including sound button) changes as well
		sound.setLocation(1230,2);
		// To set the variables of place ship stage to invalid and playmode to 2 (battle stage)
		shipNum = -1;
		playMode = 2;
		// Once battle stage starts, the user can no longer change the level
		levelLimit = true;
		// Add the point section that shows points of the user and AI
		// The point gained on the AI's panel - the user's points
		CAtp = new JLabel("Your Points  "+CAtotalPoint);
		CAtp.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
		CAtp.setForeground(Color.white);
		CAtp.setLocation(60, 370);
		CAtp.setSize(300, 100);
		// The point gained on the user's panel - the AI's points
		tp = new JLabel("Computer Points  "+totalPoint);
		tp.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
		tp.setForeground(Color.white);
		tp.setLocation(1000, 370);
		tp.setSize(300, 100);
		// The time title "time"
		timeTitle = new JLabel("Time");
		timeTitle.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
		timeTitle.setForeground(Color.white);
		timeTitle.setLocation(640, 10);
		timeTitle.setSize(200, 100);
		// The time section that keeps updating the time every second passed
		time = new JLabel("00:00");
		time.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
		time.setForeground(Color.white);
		time.setLocation(640, 50);
		time.setSize(200, 100);
		// Initialize for the battle panel is ture -> nothing happens until the user click a valid point
		initialize2 = true;
		// To let the AI place its ships as well
		ComputerAI.placeCAships ();
		// Add all the components to the panel (timer section & point section)
		add(CAtp);
		add(tp);
		add(time);
		add(timeTitle);
		repaint();
		// To resize the frame to includes two boards
		frame.setPreferredSize (new Dimension (1340, 500));
		frame.pack ();
		frame.setVisible (true);
		// To keep track of the time(in seconds)
		second = 0;
		// Thread is used to keep track of the time - used as a timer
		thread = new Thread(new Runnable() {
			public void run() {
				// keep running the timer until the user/computer win the game(or both loses it)
		        while (true) {
		        	// Update timer section every second 
		        	time.setText (String.format("%02d", second/60) +":" + String.format("%02d", second%60));	
		            try {
		                thread.sleep(1000);
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }
		            // update second and check if anyome wins 
		            second++;
		            checkWin();
		        }
		    }
		});
		// start the thread when battle stage starts
		thread.start();
	}
	// Parameter: None
	// Return: None
	// To check if either user or Computer AI win the game (have 90 points) and display winning window
	public void checkWin () {
		Object[] options = {"Next"};
		// If the user/AI wins(have gained 90 points), jump out the winning window
		if (totalPoint == 90||CAtotalPoint == 90) {
	     	if (CAtotalPoint==90) {
	     		win = JOptionPane.showOptionDialog (frame, "YOU WINS!",
			 			    "Game Over", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
			 			     options, null);
	     	} else {
	     		win = JOptionPane.showOptionDialog (frame, "COMPUTER WINS!",
			 			    "Game Over", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
			 			     options, null);
	     	}
	     	// update the best single-turn point of the user
			users.get(currentUser).setPoint(CAtotalPoint);
			// Only update the best time record when the user wins -> initially set to 10 min, the time limit
			if (CAtotalPoint == 90) {
				users.get(currentUser).setTime(second);
			}
			// Change the playmode to 4 (ranking stage), set the ranking panel 
			playMode = 4;
			setRankPanelPoint();
			// Stop the timer(thread) when someone wins/game ends
			thread.stop();
	 	}
		// !0 minutes time limit -> takes the current mark the user has and rank for the single turn highest mark
		if (second == 600) {
			win = JOptionPane.showOptionDialog (frame, "Time up!",
	 			    "Game Over", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
	 			     options, null);
			users.get(currentUser).setPoint(CAtotalPoint);
			playMode = 4;
			setRankPanelPoint();
			// Stop the timer(thread) when someone wins/game ends
			thread.stop();
		}
	}
	// Parameter: None
	// Return: None
	// To set up the ranking page that is based on point
	public void setRankPanelPoint () {
		// To set the rank panel, including the rank button, next button
		removeAll();
		add(sound);
		sound.setLocation(560, 2);
		// To get the current list of users and sort them based on which rank it is
		sortForRank = new ArrayList<User> (users.values());
		// Natural order of sorting is based on point(single-turn highest)
		Collections.sort(sortForRank);
		rankTitle = new JLabel ("Ranking (Points)");
		rankTitle.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
		rankTitle.setLocation(250, 30);
		rankTitle.setSize(300, 100);
		next = new JButton ("Next");
		next.setLocation(280, 400);
		next.setSize(80, 30);
		// When clicked the next button, go to the time ranking page
		next.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				setRankPanelTime();
			}
		});
		// To show the point rank (using JLabels) (mode 0)
		showRank(0);
		add(rankTitle);
		add(next);
		// Resize the frame back to small
		frame.setPreferredSize (new Dimension (670, 500));
		frame.pack ();
		frame.setVisible (true);
		repaint();
	}
	// Parameter: None
	// Return: None
	// To set up the ranking page that is based on the fastest time record
	public void setRankPanelTime () {
		// To set up the time ranking panel, including the exit button and the restart button
		removeAll();
		add(sound);
		exit = new JButton ("Exit");
		exit.setLocation(200, 400);
		exit.setSize(80, 30);
		// When exist clicked, exit the game
		exit.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});
		restart = new JButton ("Restart");
		restart.setLocation(380, 400);
		restart.setSize(80, 30);
		// When restart clicked, continue and jump out a window to ask if the user want to change "account"
		restart.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				Object[] options = {"Yes", "No"};
				changeUser = JOptionPane.showOptionDialog (frame, "Do you want to change the User?",
	 			       "Game Over", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
	 			        options, null);
				// If change the user, go back to the user panel; otherwise start from the place ship panel
				if (changeUser == JOptionPane.YES_OPTION) {
					reStart();
					setUserPanel();
				} else {
					reStart();
					setPlacePanel();
				}
			}
		});
		// To sort the user list by time using sortByBestTime comparator
		Collections.sort(sortForRank, new sortByBestTime());;
		rankTitle.setText("Ranking (Time)");
		add(restart);
		add(exit);
		add(rankTitle);
		// To show time rank using JLabels (mode 1)
		showRank (1);
		repaint();
	}
	// Parameter: None
	// ReturnL None
	// To initialize all the variable to prepare for a new turn of the game
	public void reStart () {
		// To reset & initialize variables to the start of the game again
		positions.clear();
		ships.clear();
		ComputerAI.clearCAships();
		initialize = true;
		initialize2 = true;
		CAtotalPoint = 0;
		levelLimit = false;
		totalPoint = 0; shipNum = 0;
		lastShipNum = 0;
		hitShip = 0;
		second = 0;
		hit = false;
		CAhit = false;
		userTurn = true;
		press = false;
		click = false;
	}
	// Parameter: number that tells if it is currently ranked by points or time
	// Return: None
	// To display the current corresponding ranking (determined by the parameter number)
	public void showRank (int num) {
		// To show the rank, format based on the mode(num)
		// the y-coordinate of the top of the ranking part (where the first one starts)  
		int top = 100;
		// The size of the user list 
		int usize = 5;
		// If the size is smaller than 5, show all; greater than 5, only shows the top 5
		if (sortForRank.size()<5) {
			usize = sortForRank.size();
		}
		// To display the rank by JLabels, mode 0 display mark, mode 1 display time
		for (int i = 0; i < usize; i++) {
			JLabel rp = new JLabel();
			rp.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
			if (num == 0 ) {
				rp.setText(String.format("%02d  %-15s%2s", (i+1), sortForRank.get(i).getName(), sortForRank.get(i).getPoints()));
			} else {
				rp.setText(String.format("%02d  %-15s%2s", (i+1), sortForRank.get(i).getName(), sortForRank.get(i).getTime()));
			}
			rp.setLocation(185, top);
			rp.setSize(500, 100);
			add(rp);
			// move down the JLabel - list the rank one by one
			top+=50;
		}
	}
	// Parameter: None
	// Return: The position of the point the user chooses to hit
	// To calculate the user choosen displayed point location and its color (on CA panel)
	public Position userChooseShip () {
		// The coordinate of the position the user clicked to guess the ship
		int x = 670+boardx+(pointX-(670+boardx))/squareSize*squareSize+(squareSize/5*2);
		int y = boardy+(pointY-boardy)/squareSize*squareSize+(squareSize/5*2);
		// If this point is valid/not clicked beofre, if hit->location & red point; if not hit -> location & white point
		if (!positions.contains(new Position(x, y, Color.white))) {
			if (CAhit) {
				return new Position(x, y, Color.red);
			} else {
				return new Position(x, y, Color.white);
			}
		}
		// If the point was clicked before, return nothing/null
		return null;
	}
	// Constructor: To download background images and music, and set up the basic panel and related components like setting
	public Main () {
		// To set the frame
		frame.setPreferredSize (new Dimension (670, 500));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		setLayout(null);
		// To download all the background images
		MediaTracker tracker = new MediaTracker (this);
		ocean = Toolkit.getDefaultToolkit ().getImage ("ocean.jpg");
		tracker.addImage (ocean, 0);
		cover = Toolkit.getDefaultToolkit ().getImage ("start.png");
		tracker.addImage (cover, 0);
		user = Toolkit.getDefaultToolkit ().getImage ("user.png");
		tracker.addImage (user, 0);
		ranking = Toolkit.getDefaultToolkit ().getImage ("ranking.jpg");
		tracker.addImage (ranking, 0);
		// To download the BGM sound effect 
		try {
			AudioInputStream s = AudioSystem.getAudioInputStream(new File ("BGM1.wav"));
			BGM = AudioSystem.getClip();
			BGM.open(s);
		} 
		catch (Exception e) {
		}
		try{
		    tracker.waitForAll ();
		}catch (InterruptedException e){
		}
		// If it is the start panel, add the sound button and start the game button
		if (playMode==0) {
			// BGM defaultly starts playing when the game starts
			BGM.setFramePosition (0); 
			BGM.loop(Clip.LOOP_CONTINUOUSLY);
			start1 = new JButton("Start");
			start1.setLocation(278, 400);
			start1.setSize(100, 30);
			// When start is hit, continue to the user enter name panel
			start1.addActionListener (new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					setUserPanel();
				}
			});
			sound = new JButton("Music Off");
			sound.setLocation(560, 2);
			sound.setSize(90, 30);
			// Click the sound button to turn on/off the BGM  
			sound.addActionListener (new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					if (bgm) {
						sound.setText("Music On");
						BGM.stop();
						bgm = false;
					} else {
						// Continues from where stopped instead of replay from the beginning
						sound.setText("Music Off");
						BGM.start (); 
						BGM.loop(Clip.LOOP_CONTINUOUSLY);
						bgm = true;
					}
				}
			});
			// To set up the level menu for the game
			JMenuItem level1, level2, level3;
			level1 = new JMenuItem ("level 1");
			level2 = new JMenuItem ("level 2");
			level3 = new JMenuItem ("level 3");
			// Level one: set board to 5 x 5
			level1.addActionListener (new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					if (!levelLimit) {
						initialize = true;	
						ships.clear();
						row = 5;
						column = 5;
						boardx = 200;
						boardy = 105;
						repaint();
					}
				}
			});	
			// level 2: set board to 6 x 6
			level2.addActionListener (new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					if (!levelLimit) {
						initialize = true;
						ships.clear();
						row = 6;
						column = 6;
						boardx = 170;
						boardy = 75;
						repaint();
					}
				}
			});
			// level 3: set board to 7 x 7
			level3.addActionListener (new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					if (!levelLimit) {
						initialize = true;
						ships.clear();
						row = 7;
						column = 7;
						boardx = 140;
						boardy = 45;	
						repaint();
					}
					}
			});
			// Set the rule menu and window for explaining how to play the game
			Object[] options = {"OK"};
			JMenuItem howToPlay;
			howToPlay = new JMenuItem("How to play");
			howToPlay.addActionListener (new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					int ruleWindow = JOptionPane.showOptionDialog (frame, "Move the battle ships on the board and hit the start button to\nstart battling with the computer by clicking at grids that you\nthink may have ship 'hides' behind\n\ntips: rotation would rotate the last ship you have moved\n        level can not be reset once the abttle part starts\n        Time limit is 10 minutes, Good luck! :D",
			 			    "How to play", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
			 			     options, null);
				}
			});
			// Make the main menu, including a level part and a rule/how to play part
			JMenu level = new JMenu ("Level");
			level.add(level1);
			level.addSeparator();
			level.add(level2);
			level.addSeparator();
			level.add(level3);
			JMenu rule = new JMenu ("Rules");
			rule.add(howToPlay);
			JMenu setting = new JMenu ("Setting");
			setting.add(level);
			setting.add(rule);
			JMenuBar mainMenu = new JMenuBar();
			mainMenu.add(setting);
			frame.setJMenuBar(mainMenu);
			add(sound);
			add(start1);
		}
		// Add the mouseListener 
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	// Parameter: graphic variable for drawing/displaying
	// Return: None
	// Draw the board an updates it
	public void paintComponent (Graphics g)
    {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		// If it is palce panel or battle panel, printout the board
		if (playMode!=0&&playMode!=3&&playMode!=4) {
			g.drawImage(ocean, 0, 0, 670, 500, this);
			int xPos = 0;
			int yPos = 0;
			// Draw the grids one by one
			for (int i = 0; i < row; i++) {
				for (int i1 = 0; i1 < column; i1++) {
					xPos = boardx + i1*squareSize;
					yPos = boardy + i*squareSize;
					g.setColor(Color.white);
					g.drawRect(xPos, yPos, squareSize, squareSize);
				}
			}
			// If it is the statrt of the game when there is no ship yet, add the ships to the list
			if (initialize) {
				ships.add(new Ship(new Rectangle(boardx, boardy, 100, 50), 20, 2));
				ships.add(new Ship(new Rectangle(boardx+150, boardy, 50, 50), 50, 1));
				ships.add(new Ship(new Rectangle(boardx, boardy+100, 50, 100), 20, 2));
				initialize = false;
			}
			// To draw out the ships
			for (int i = 0; i < ships.size(); i++) {
				g2.setColor(Color.black);
				g2.draw(ships.get(i).getshipShape());
				g2.setColor(Color.white);
				g2.fill(ships.get(i).getshipShape());
			} 
			// Display the Battle panel 
			if (playMode == 2) {
				g.drawImage(ocean, 670, 0, 670, 500, this);
				// Display the second board 
				for (int i = 0; i < row; i++) {
					for (int i1 = 0; i1 < column; i1++) {
						xPos = 670 + boardx + i1*squareSize;
						yPos = boardy + i*squareSize;
						g.setColor(Color.white);
						g.drawRect(xPos, yPos, squareSize, squareSize);
					}
				}
				// No move/change(do not start) the battle process until the user clicked a valid point
				if (!initialize2) {
					point = null;
					// To record the location of the point the user clicked
					if (userTurn&&click) {
						// Porcessing user turn, so next turn is AI's turn
						userTurn = false;
						click = false;
						point = userChooseShip ();
						// If the clicked point is invalid, nothing happens and it is still the user's turn
						if (point==null) {
							userTurn = true;
						} 
					} else if (!userTurn){
						// Wait some time before the AI guess the ship -> for the user to see clearly what is happnening
						try {
				                thread.sleep(200);
				         } catch (InterruptedException e) {
				                e.printStackTrace();
				        }
						// Processing AI's turn, so next is the user's turn
						userTurn = true;
						click = false;
						// Call didferent guessing method based on if any ship is hit
						// No ship hit -> guess randomly; ship get hit -> guess the surrounding
						if (hit) {
							point = ComputerAI.guessHitShip();
						} else {
							point = ComputerAI.guessShip ();
						}
					}
					// If the clciked point is valid (ex. within the board, no repeat), add it to the list and display it
					if (point!=null) {
						positions.add(point);
						repaint();
					} 
					// To display the points (hitting points)
					for (Position po:positions) {
						g.setColor(po.getColor());
						g.drawOval(po.getX(), po.getY(), radius, radius);
						g.fillOval(po.getX(), po.getY(), radius, radius);
					}
					// To update the point
					updatePoint();
					}		
			}
		// To display different background image based on the panel/stage
		} else if (playMode == 0){
			g.drawImage(cover, 0, 0, 670, 500, this);
		} else if (playMode==3) {
			g.drawImage(user, 0, 0, 670, 500, this);
		} else if (playMode == 4) {
			g.drawImage(ranking, 0, 0, 670, 500, this);
		}
    }
	public static void main(String[] args) {
		// To create the frame and set the class itself to a panel
		frame = new JFrame("Battle Ship");
		Main grid = new Main();
		frame.add (grid);
		frame.pack ();
		frame.setVisible (true);
	}
	// Parameter: the user’s mouse is pressed
	// Return: None
	// To update the ship and call methods to check which ship is selected when the user starts to move ships
	@Override
	public void mousePressed(MouseEvent e) {
		// If it is the placing ship panel, mpuse pressed means to start dragging and moving the ship
		if (playMode == 1) {
			press = true;
			// To find out which ship the user is dragging
			shipNum = shipNumber (e);
			// If the user is dragging a ship, update the ship (ex. location)
			if (shipNum!=-1) {
				// To set the ship to rotate to the latest valid moved ship
				lastShipNum = shipNum;
				// The rectangle that gets updates as the user moves the ship -> only if its final position is valid, the actual ship gets updated
				updateRect = new Rectangle (shipx, shipy, ships.get(shipNum).getWidth(), ships.get(shipNum).getHeight());
				prex = ships.get(shipNum).getX()-e.getX();
				prey = ships.get(shipNum).getY()-e.getY();
				updateShip(e);
			}
		}
	}
	// Parameter: the user’s mouse is dragged
	// Return: None
	// To move and update the ship when the user is dragging and placing the ship 
	@Override
	public void mouseDragged(MouseEvent e) {
		// When it is the place ship panel and the user is dragging a ship...
		if (playMode==1&&shipNum!=-1) {
			// Once the mouse is not on the ship (not "dragging" the ship), the ship immedialy stops(shipNum = -1 -> no ship is being dragged) and goes to the closest valid location
			if(!updateRect.contains(e.getX(), e.getY())) {
				press= false;
				updateShip(e);
				shipNum = -1;
			// Otherwise, update the ship for update - > not the actucal one, only if the move is considered valid, the actual ship gets reset
			} else {
				updateShip(e);
			}
		}
	}
	// Parameter: the user’s mouse is released
	// Return: None
	// To stop the ship and call methods to check if the ship is in proper position when the mouse is released
	@Override
	public void mouseReleased(MouseEvent e) {
		// When it is the place panel and the user dragged a ship but now release the mouse...
		if (playMode==1&&shipNum!=-1) {
			// Update the ship and do the final realse check to make sure the actual ship lies in a valid location
			press = false;
			updateShip(e);
		} 
	}
	// Parameter: the user’s mouse is clicked
	// Return: None
	// To record the chosen hitting point that the user clicks and call method to check if any ship is hit
	@Override
	public void mouseClicked(MouseEvent e) {
		// When it is the battle ship panel and it is the user's turn to click ...
		if (playMode == 2 && userTurn) {
			// Start the battle process
			initialize2 = false;
			// If the click point is within the board, continue
			if ((e.getX()<670+boardx+squareSize*column&&e.getX()>670+boardx)&&(e.getY()<boardy+squareSize*row&&e.getY()>boardy)) {
				userTurn = true;
				click = true;
				// To check if the user hit any ship, if yes, add the ship's hit time by one, and if the ship's to be hit time is reached, the ship is completely hit down and the user gets point
				CAhit = hitOrNot(e.getX(), e.getY(), ComputerAI.getCAships());
				if (CAhit) {
					ComputerAI.getCAships().get(hitShip).setHitTime();
					if (ComputerAI.getCAships().get(hitShip).getHitTime()==ComputerAI.getCAships().get(hitShip).getToHitTimes()) {
						CAtotalPoint+=ComputerAI.getCAships().get(hitShip).getPoints();
					}
				}
				// The coordinate of the point the user clicked and to display a point within the middle of the grid(little ones)
				pointX = e.getX();
				pointY = e.getY();
				repaint();
			}
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}
}
