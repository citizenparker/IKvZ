import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;

/**
	Instances of IKVZ create a window and begin using it to play a video game called Ikari Warriors
	vs. ZOMBIES! They do so upon creation; no further input from a main function is neccessary.<br>
	Instances of IKVZ can only be properly created when all files in the distribution package are in
	in the running director of IKVZ and all classes in this package are available.<br>
	Most members and methods will behave unpredictably if they are accessed prior to calling start()

	For information on using / modifying the source code of IKVZ, please refer to the programmer's manual.

	@author Scott Parker
	@version 1.0

*/

public class IKVZ extends JFrame implements ActionListener, KeyListener, MouseListener
{
	// Counts the total amount the screen has been shifted by walking;
	private static int amtShifted = 0;
	// Activates console output of mouse clicks - useful for determining proper enemy placement
	private static final boolean DEVELOPER =		false;

	/** The desired width of the game window */
	public static final int WIDTH =					700;
	/** The desired height of the game window */
	public static final int HEIGHT = 				500;
	/** The resolution width the screen must meet or exceed */
	public static final int MIN_WIDTH = 			1024;
	/** The resolution height the screen must meet or exceed */
	public static final int MIN_HEIGHT =			768;
	/** The size of the black border between the edges of the frame and actual playing area, in pixels */
	public static final int BORDER_SIZE =			50;

	/** The state for displaying the title screen */
	public static final byte STATE_TITLE =			0;
	/** The state for pausing the game */
	public static final byte STATE_PAUSE =			1;
	/** The state for running the game */
	public static final byte STATE_RUN =			2;
	/** The state for creating a new game */
	public static final byte STATE_NEW =			3;
	/** The state for continuing the game */
	public static final byte STATE_DEAD =			4;

	/** The maximum amount of health the player starts with */
	public static final int HEALTH_AMOUNT =			100;
	/** The damage inflicted per timer cycle by a zombie on a player */
	public static final int HEALTH_DAMAGE =			3;
	/** The length of the health bar, in pixels */
	public static final int HEALTH_BAR_LENGTH =		100;
	/** The size of the border around the health bar */
	public static final int HEALTH_BAR_BORDER =		2;
	/** The x coordinate of the health bar text */
	public static final int HEALTH_TEXT_X =			15;
	/** The Y coordinate of the health bar text */
	public static final int HEALTH_TEXT_Y =			15;

	/** The indicator for controlling the game through the keyboard arrow keys */
	public static final byte CONTROLS_ARROWS =		0;
	/** The indicator for controlling the game through the numbers on the keyboard */
	public static final byte CONTROLS_NUMBERS =		1;
	/** The indicator for controlling the game using keyboard DAW keys */
	public static final byte CONTROLS_LETTERS =		2;

	/** The speed in pixels / cycle a player or the world about him will move */
	public static final int SHIFT_AMOUNT =			8;

	/** The name of this game */
	public static final String PROGRAM_TITLE =		"Ikari Warriors VS. ZOMBIES!";
	/** The text in the "About" dialog */
	public static final String PROGRAM_ABOUT =		"Almost all images copyrighted by various parties, shamefully assembled by Scott Parker.\nAll programming by Scott Parker\n(for more, please see: www.missouri.edu/~spp9fa)";
	/** The text displayed upon winning */
	public static final String PROGRAM_WIN =		"Congratulations!\n You found Ash and that's probably good for something!\nYou are one of life's winners!!!";

	// Locations of various files - explanations where needed
	private static final String IMAGE_BACKGROUND =	"backgrnd.jpg";
	private static final String IMAGE_DEAD_PLAYER =	"dead.gif";
	private static final String IMAGE_DEAD_ZOMBIE =	"dzombie.gif";
	private static final String IMAGE_GIRDER =		"girder.gif";
	private static final String IMAGE_LADDER =		"ladder.gif";
	private static final String IMAGE_LOGO =		"ikvzlogo.gif";		//Title logo
	private static final String IMAGE_PLAYER_R1 =	"right1.gif";		//Player - moving right 1
	private static final String IMAGE_PLAYER_R2 =	"right2.gif";		//Player - moving right 2
	private static final String IMAGE_PLAYER_L1 = 	"left1.gif";		//etc...
	private static final String IMAGE_PLAYER_L2 =	"left2.gif";
	private static final String IMAGE_PLAYER_U1 =	"up1.gif";
	private static final String IMAGE_PLAYER_U2 =	"up2.gif";
	private static final String IMAGE_ZOMBIE_R1 =	"zombier.gif";		//Zombie - moving right 1
	private static final String IMAGE_ZOMBIE_L1 =	"zombiel.gif";		//etc...
	private static final String MUSIC_CLIP =		"music.wav";		//Theme music
	private static final String GOAL_CLIP	=		"sugar.wav";		//End clip
	private static final String ABBA_CLIP =			"abba.wav";			//Continue clip
	private static final String IMAGE_GOAL =		"ash.gif";
	private static final String IMAGE_ABBA =		"abba.jpg";			//Abba album used in continues

	// Length of ABBA_CLIP audio in milliseconds
	private static final int ABBA_TIME =				3748;

	/** The number of different directions for the Player images */
	public static final int IMAGE_PDIRECTIONS =		3;
	/** The total number of Player images */
	public static final int IMAGE_PCOUNT =			6;
	/** The total number of Zombie images */
	public static final int IMAGE_ZCOUNT =			2;

	/** Direction to stop all movement */
	public static final byte DIR_STOP =				-1;
	/** See DIR_LEFT */
	public static final byte DIR_RIGHT =			0;
	/** Direction to indicate left movement */
	public static final byte DIR_LEFT =				1;
	/** See DIR_LEFT */
	public static final byte DIR_UP =				2;

	/** Percentage of screen (expressed in 0.XX form) that Player can occupy without moving the world */
	public static final double MOVE_RATIO =			.65;
	/** Percentage odds (expressed in 0.XX form) that a Zombie will change his direction */
	public static final double ZDIR_CHANGE_ODDS =	.30;

	/** Width in pixels of a bullet */
	public static final int BULLET_WIDTH =			5;
	/** Height in pixels of a bullet */
	public static final int BULLET_HEIGHT =			3;
	/** The location on the Player image that a bullet should come from, in pixels from the top */
	public static final int BULLET_FIRE_FROM_Y =	26;

	// The background music, the end audio clip, and the continue clip respectively
	private static AudioClip music;
	private static AudioClip end;
	private static AudioClip dancingQueen;

	/** The PlayPanel displaying all graphics */
	static PlayPanel screen;
	/** The menu for the IKVZ instance */
	static GameMenu menu;

	/** The current instance of IKVZ running */
	private static IKVZ current;

	/** Array of current zombie images, referenced by DIR_ tags */
	static Image zombieImage[];
	/** Array of current player images, referenced by DIR_ tags */
	static Image playerImage[];
	/** Image of a dead Player */
	static Image deadPlayerImage;
	/** Image of a dead Zombie */
	static Image deadZombieImage;
	/** Image of the logo */
	static Image logoImage;
	/** Image of the background */
	static Image backgroundImage;
	/** Image of a girder */
	static Image girderImage;
	/** Image of a ladder */
	static Image ladderImage;
	/** Image of the end of-level-goal */
	static Image goalImage;
	/** Image of the ABBA album used in continuing */
	static Image abbaImage;

	/** The instance of player being used by IKVZ*/
	static Player player;
	/** The instances of Girder being used by IKVZ*/
	static Girder girders[];
	/** The instances of Ladder being used by IKVZ*/
	static Ladder ladders[];
	/** The instance of Floor being used by IKVZ*/
	static Floor  floor;
	/** The instance of Goal being used by IKVZ*/
	static Goal   goal;
	/** The instance of ABBA being used by IKVZ */
	static ABBA	  abba;
	/** The instances of Zombie being used by IKVZ */
	static Vector zombies;
	/** The instances of Bullet being used by IKVZ */
	static Vector bullets;
	/** The collection of non-mobile Zombie instances being used by IKVZ */
	static Vector deadZombies;


	//--------------------------------
	// INITIAL COORDINATE LIST BEGINS
	//--------------------------------

	private static int COORD_PLAYER_X =				50;
	private static int COORD_PLAYER_Y =				50;

	private static int COORD_ZOMBIE0_X =			100;
	private static int COORD_ZOMBIE0_Y =			120;
	private static int COORD_ZOMBIE1_X =			260;
	private static int COORD_ZOMBIE1_Y =			120;
	private static int COORD_ZOMBIE2_X =			180;
	private static int COORD_ZOMBIE2_Y =			270;
	private static int COORD_ZOMBIE3_X =			230;
	private static int COORD_ZOMBIE3_Y =			270;
	private static int COORD_ZOMBIE4_X =			300;
	private static int COORD_ZOMBIE4_Y =			270;

	private static int COORD_ZOMBIE5_X =			370;
	private static int COORD_ZOMBIE5_Y =			120;
	private static int COORD_ZOMBIE6_X =			420;
	private static int COORD_ZOMBIE6_Y =			120;
	private static int COORD_ZOMBIE7_X =			420;
	private static int COORD_ZOMBIE7_Y =			270;
	private static int COORD_ZOMBIE8_X =			490;
	private static int COORD_ZOMBIE8_Y =			270;
	private static int COORD_ZOMBIE9_X =			520;
	private static int COORD_ZOMBIE9_Y =			120;

	private static int COORD_ZOMBIE10_X =			530;
	private static int COORD_ZOMBIE10_Y =			270;
	private static int COORD_ZOMBIE11_X =			590;
	private static int COORD_ZOMBIE11_Y =			120;
	private static int COORD_ZOMBIE12_X =			590;
	private static int COORD_ZOMBIE12_Y =			270;
	private static int COORD_ZOMBIE13_X =			650;
	private static int COORD_ZOMBIE13_Y =			120;
	private static int COORD_ZOMBIE14_X =			690;
	private static int COORD_ZOMBIE14_Y =			120;

	private static int COORD_ZOMBIE15_X =			810;
	private static int COORD_ZOMBIE15_Y =			270;
	private static int COORD_ZOMBIE16_X =			900;
	private static int COORD_ZOMBIE16_Y =			120;
	private static int COORD_ZOMBIE17_X =			950;
	private static int COORD_ZOMBIE17_Y =			270;
	private static int COORD_ZOMBIE18_X =			1040;
	private static int COORD_ZOMBIE18_Y =			270;
	private static int COORD_ZOMBIE19_X =			1130;
	private static int COORD_ZOMBIE19_Y =			270;

	private static int COORD_ZOMBIE20_X =			1285;
	private static int COORD_ZOMBIE20_Y =			270;
	private static int COORD_ZOMBIE21_X =			1050;
	private static int COORD_ZOMBIE21_Y =			120;
	private static int COORD_ZOMBIE22_X =			1170;
	private static int COORD_ZOMBIE22_Y =			120;
	private static int COORD_ZOMBIE23_X =			1280;
	private static int COORD_ZOMBIE23_Y =			270;
	private static int COORD_ZOMBIE24_X =			1370;
	private static int COORD_ZOMBIE24_Y =			120;

	private static int COORD_ZOMBIE25_X =			1420;
	private static int COORD_ZOMBIE25_Y =			270;
	private static int COORD_ZOMBIE26_X =			1470;
	private static int COORD_ZOMBIE26_Y =			270;
	private static int COORD_ZOMBIE27_X =			1540;
	private static int COORD_ZOMBIE27_Y =			270;
	private static int COORD_ZOMBIE28_X =			1580;
	private static int COORD_ZOMBIE28_Y =			120;
	private static int COORD_ZOMBIE29_X =			1670;
	private static int COORD_ZOMBIE29_Y =			120;

	private static int COORD_GIRDER0_X =			100;
	private static int COORD_GIRDER0_Y =			210;
	private static int COORD_GIRDER1_X =			375;
	private static int COORD_GIRDER1_Y =			210;
	private static int COORD_GIRDER2_X =			570;
	private static int COORD_GIRDER2_Y =			210;
	private static int COORD_GIRDER3_X =			875;
	private static int COORD_GIRDER3_Y =			210;
	private static int COORD_GIRDER4_X =			1070;
	private static int COORD_GIRDER4_Y =			210;
	private static int COORD_GIRDER5_X =			1265;
	private static int COORD_GIRDER5_Y =			210;
	private static int COORD_GIRDER6_X =			1542;
	private static int COORD_GIRDER6_Y =			210;

	private static int COORD_LADDER0_X =			100;
	private static int COORD_LADDER0_Y =			210;
	private static int COORD_LADDER1_X =			410;
	private static int COORD_LADDER1_Y =			210;
	private static int COORD_LADDER2_X =			650;
	private static int COORD_LADDER2_Y =			210;
	private static int COORD_LADDER3_X =			940;
	private static int COORD_LADDER3_Y =			210;
	private static int COORD_LADDER4_X =			1180;
	private static int COORD_LADDER4_Y =			210;
	private static int COORD_LADDER5_X =			1600;
	private static int COORD_LADDER5_Y =			210;

	private static int COORD_GOAL_X =				1800;
	private static int COORD_GOAL_Y =				170;

	//------------------------------
	// INITIAL COORDINATE LIST ENDS
	//------------------------------


	/** Total number of girders used in floor */
	public static int COUNT_FLOOR =					5;
	/** Total number of girders (excluding floor) in existence */
	public static int COUNT_GIRDER =				7;
	/** Total number of ladders in existence */
	public static int COUNT_LADDER =				6;

	/** The current state */
	static byte state =								STATE_TITLE;
	/** The current controls scheme */
	static byte controls =							CONTROLS_ARROWS;
	/** Flag indicating sound is enabled */
	static boolean sound =							true;

	// The previous state for use with pauseToggle() */
	private static byte prevState =					STATE_PAUSE;

	// The timer used to update the game */
	private static javax.swing.Timer timer;
	// The delay between firings of the timer */
	private static final int DELAY_TIMER =			50;

	/** The delay in timer cycles of animations */
	public static final int DELAY_ANIMATION =		6;
	/** The delay in timer cycles to show player upon death */
	public static final int DEAD_TIME =				50;

	// Counts cycles currently used for showing death
	private static int deadCounter = 				0;

	// Loads game upon creation from start()
	private IKVZ()
	{

		URL musicURL = 			null;	// URLs for AudioClip initilization
		URL endURL = 			null;
		URL dancingQueenURL =	null;

		try
		{
			musicURL = 			new URL("file", "localhost", MUSIC_CLIP); // Set URLs to valid files
			endURL =			new URL("file", "localhost", GOAL_CLIP);
			dancingQueenURL =	new URL("file", "localhost", ABBA_CLIP);
		}
		catch(MalformedURLException e)
		{
			System.exit(1);		// Don't really handle any errors, just quit
		}

		// Get the new Audio Clips
		music = Applet.newAudioClip(musicURL);
		end = Applet.newAudioClip(endURL);
		dancingQueen = Applet.newAudioClip(dancingQueenURL);

		// Prepare Timer for use at DELAY_TIMER intervals
		timer = new javax.swing.Timer(DELAY_TIMER, this);
		timer.setInitialDelay(0);
		timer.setCoalesce(true);

		current = this;		// set current value to this instance

		// Create Image arrays
		zombieImage = new Image[IMAGE_ZCOUNT];
		playerImage = new Image[IMAGE_PCOUNT];

		// Retrieve the proper images for each image
		Toolkit tk = Toolkit.getDefaultToolkit();
		zombieImage[DIR_RIGHT] =				 		tk.getImage(IMAGE_ZOMBIE_R1);
		zombieImage[DIR_LEFT] = 						tk.getImage(IMAGE_ZOMBIE_L1);
		playerImage[DIR_RIGHT] =				 		tk.getImage(IMAGE_PLAYER_R1);
		playerImage[DIR_LEFT] = 						tk.getImage(IMAGE_PLAYER_L1);
		playerImage[DIR_UP] = 							tk.getImage(IMAGE_PLAYER_U1);
		playerImage[DIR_RIGHT + IMAGE_PDIRECTIONS] = 	tk.getImage(IMAGE_PLAYER_R2);
		playerImage[DIR_LEFT + IMAGE_PDIRECTIONS] =		tk.getImage(IMAGE_PLAYER_L2);
		playerImage[DIR_UP + IMAGE_PDIRECTIONS] = 		tk.getImage(IMAGE_PLAYER_U2);
		deadPlayerImage =								tk.getImage(IMAGE_DEAD_PLAYER);
		deadZombieImage =								tk.getImage(IMAGE_DEAD_ZOMBIE);
		logoImage = 									tk.getImage(IMAGE_LOGO);
		backgroundImage = 								tk.getImage(IMAGE_BACKGROUND);
		ladderImage =									tk.getImage(IMAGE_LADDER);
		girderImage =									tk.getImage(IMAGE_GIRDER);
		goalImage =										tk.getImage(IMAGE_GOAL);
		abbaImage =										tk.getImage(IMAGE_ABBA);

		// Check all images with MediaTracker to force them to load
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(ladderImage, 0);
		mt.addImage(girderImage, 0);
		mt.addImage(logoImage, 0);
		mt.addImage(backgroundImage, 0);
		mt.addImage(deadPlayerImage, 0);
		mt.addImage(deadZombieImage, 0);
		mt.addImage(goalImage,0);
		mt.addImage(abbaImage, 0);
		for(int i = 0; i < IMAGE_PCOUNT; i++)
		{
			mt.addImage(playerImage[i], 0);
		}
		for(int j = 0; j < IMAGE_ZCOUNT; j++)
		{
			mt.addImage(zombieImage[j], 0);
		}
		while( !mt.checkAll(true) )
		{}
		if(mt.isErrorAny())
		{
			System.out.println("Error in image loading. Please check that package was distributed correctly and contact spp9fa@mizzou.edu");
			System.exit(1);
		}


		// Sets the class dimensions to the appropriate images
		Player.setDimensionsTo(playerImage[0]);
		Zombie.setDimensionsTo(zombieImage[0]);
		Ladder.setDimensionsTo(ladderImage);
		Girder.setDimensionsTo(girderImage);
		Goal.setDimensionsTo(goalImage);
		Bullet.setDimensionsTo(BULLET_WIDTH, BULLET_HEIGHT);
		abba.setDimensionsTo(abbaImage);

		// Create the frame components
		menu	= new GameMenu();
		screen	= new PlayPanel();


		// Ensure the proper resolution is met
		if(tk.getScreenSize().width < MIN_WIDTH || tk.getScreenSize().height < MIN_HEIGHT)
		{
			JOptionPane.showMessageDialog(null, PROGRAM_TITLE+" must be run in at least "+MIN_WIDTH+"x"+MIN_HEIGHT+" resolution.", PROGRAM_TITLE+" ERROR", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		// Set Title, Size, and center window
		setTitle(PROGRAM_TITLE);
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);

		// Force the content pane to be our PlayPanel, and set the menu
		setContentPane(screen);
		setJMenuBar(menu);

		// Exit function on window closing
		addWindowListener( new WindowAdapter(){
			public void windowClosing(WindowEvent evt)
			{
				System.exit(0);
			}});

		// Force our original size upon resizing
		addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent evt)
			{
				setSize(WIDTH, HEIGHT);
			}});

		// This component listens to itself to retrieve Key and Mouse events
		addKeyListener(this);
		addMouseListener(this);

		// Display the frame
		show();

		// Start the timer
		timer.start();


	}

	public static void main(String[] args)
	{
		// Set L&F to system default
		try
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		}
		catch(Exception e){}

		// Begin the game
		IKVZ.start();

	}

/**	@return a reference to the current instance of IKVZ*/
//	-----------------------------------------------------
	public static IKVZ get()
	{
		return current;
	}

/** Starts IKVZ, if not started already*/
//	-------------------------------------
	public static void start()
	{
		if(IKVZ.current == null)
		{
			new IKVZ();
		}
	}

/** Turns the audio On. This function will begin music if the player is currently playing the game */
//	-------------------------------------------------------------------------------------------------
	public static void audioOn()
	{
		sound = true;
		if(state == STATE_RUN)
			music.play();
	}

/** Turns off any currently playing audio */
//	----------------------------------------
	public static void audioOff()
	{
		sound = false;
		music.stop();
		end.stop();
		dancingQueen.stop();
	}

/** Pauses the game, when possible. This is only when the game is running and IKVZ is in the running state */
//	---------------------------------------------------------------------------------------------------------
	public static void pauseToggle()
	{
		switch(state)
		{
			case STATE_PAUSE:
				state = prevState;
				prevState = STATE_PAUSE;
				GameMenu.miPause.setText("Pause");
				break;
			case STATE_RUN:
				prevState = state;
				state = STATE_PAUSE;
				GameMenu.miPause.setText("Unpause");
				break;
		}

	}

/** Listens to and processes the Timer events based on current state
	@param evt	The event associated with the Timer. (Not used)*/
//	----------------------------------------------------------------
	public void actionPerformed(ActionEvent evt)
	{
		switch(state)
		{
			case STATE_RUN:
				runGame();
				if(!(IKVZ.player.isAlive()) )
				{
					// Waits the specified time, then goes to the continue screen
					if(++deadCounter > IKVZ.DEAD_TIME)
					{
						music.stop();
						state = STATE_DEAD;
						deadCounter = 0;
					}
				}
				else if(player.intersects(goal))
				{
					// Plays appropriate sounds, resets to the title, and shows a victor message
					music.stop();
					if(sound)
						end.play();

					state = STATE_TITLE;
					JOptionPane.showMessageDialog(null, PROGRAM_WIN , "A Winner is you!", JOptionPane.PLAIN_MESSAGE );
				}
				break;

			case STATE_NEW:
				// Start a new game, then run this new game
				newGame();
				state = STATE_RUN;
				break;

			case STATE_DEAD:
				// Move the ABBA album and if time runs out, go back to the title
				abba.move();
				if(++deadCounter / 3 > IKVZ.DEAD_TIME)
				{
					state = STATE_TITLE;
					deadCounter = 0;
				}
				break;

		}

		// Ensure the focus for mouse and keyboard events
		requestFocusInWindow();

		// Redraw the screen
		screen.repaint();
	}

/** Handles keyboard output and dispatches those events to <tt>IKVZ.player</tt>
	@see Player
	@param evt The event indicating what keys were pressed */
//	---------------------------------------------------------------------------
	public void keyPressed(KeyEvent evt)
	{
		// Only process keyboard input during game
		if(state != STATE_RUN)
		{
			return;
		}

		// Fire approrpriate message to player
		if( evt.getKeyCode() == getKey(IKVZ.DIR_RIGHT) )
		{
			IKVZ.player.setDirection(IKVZ.DIR_RIGHT);
		}
		else if( evt.getKeyCode() == getKey(IKVZ.DIR_LEFT) )
		{
			IKVZ.player.setDirection(IKVZ.DIR_LEFT);
		}
		else if( evt.getKeyCode() == getKey(IKVZ.DIR_UP) )
		{
			IKVZ.player.setDirection(IKVZ.DIR_UP);
		}
		else if(evt.getKeyCode() == KeyEvent.VK_SPACE )
		{
			IKVZ.player.fire();
		}

	}

/** Processes a direction flag into a KeyEvent event, based upon the current control scheme
	@param direction the DIR_ flag to get the key for
	@return an integer representing the Virtual Key to be pressed corresponding to <tt>direction</tt>
	<br><br> returns -1 if error*/
//	----------------------------------------------------------------------------------------------------
	public int getKey(byte direction)
	{
		switch(direction)
		{
			case DIR_RIGHT:
				switch(controls)
				{
					case CONTROLS_ARROWS:
						return KeyEvent.VK_RIGHT;
					case CONTROLS_NUMBERS:
						return KeyEvent.VK_6;
					case CONTROLS_LETTERS:
						return KeyEvent.VK_D;
				}
				break;

			case DIR_LEFT:
				switch(controls)
				{
					case CONTROLS_ARROWS:
						return KeyEvent.VK_LEFT;
					case CONTROLS_NUMBERS:
						return KeyEvent.VK_4;
					case CONTROLS_LETTERS:
						return KeyEvent.VK_A;
				}
				break;

			case DIR_UP:
				switch(controls)
				{
					case CONTROLS_ARROWS:
						return KeyEvent.VK_UP;
					case CONTROLS_NUMBERS:
						return KeyEvent.VK_8;
					case CONTROLS_LETTERS:
						return KeyEvent.VK_W;
				}
				break;

		}

		return -1;
	}

/** Shifts everything in the gameworld excluding the player
	@param amount The amount in pixels by which to shift everything*/
//	-----------------------------------------------------------------
	public static void shiftAll(int amount)
	{
		amtShifted -= amount;

		for(int i = 0; i < girders.length; i++)
		{
			girders[i].shiftBy(amount, 0);
		}

		for(int i = 0; i < ladders.length; i++)
		{
			ladders[i].shiftBy(amount, 0);
		}

		floor.shiftBy(amount);

		for(int i = 0; i < zombies.size(); i++)
		{
			((Zombie)zombies.get(i)).shiftBy(amount, 0);
		}

		for(int i = 0; i < deadZombies.size(); i++)
		{
			((Zombie)deadZombies.get(i)).shiftBy(amount, 0);
		}

		for(int i = 0; i < bullets.size(); i++)
		{
			((Bullet)bullets.get(i)).shiftBy(amount, 0);
		}

		goal.shiftBy(amount, 0);

		screen.shiftBackgroundBy(-2);

	}

/** Called upon release of any key. Stops the player if the key was anything but the space key
	@param evt The KeyEvent causing the function to be called*/
//	------------------------------------------------------------------------------------------
	public void keyReleased(KeyEvent evt)
	{
		if(state != STATE_RUN || evt.getKeyCode() == KeyEvent.VK_SPACE)
		{
			return;
		}
		IKVZ.player.setDirection(IKVZ.DIR_STOP);
	}

/** Does absolutely nothing
	@param evt The KeyEvent causing the function to be called*/
//	-----------------------------------------------------------
	public void keyTyped(KeyEvent evt)
	{

	}

/*	Initializes all elements needed for play to begin */
//	----------------------------------------------------
	private void newGame()
	{
		// Create the appropriate arrays
		player =	new Player(COORD_PLAYER_X, COORD_PLAYER_Y);
		girders =	new Girder[COUNT_GIRDER];
		ladders =	new Ladder[COUNT_LADDER];
		zombies =	new Vector();
		bullets =	new Vector();
		deadZombies = new Vector();

		//-------------------------
		// BEGIN ADDING OF ELEMENTS
		//-------------------------

		zombies.add( new Zombie(COORD_ZOMBIE0_X, COORD_ZOMBIE0_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE1_X, COORD_ZOMBIE1_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE2_X, COORD_ZOMBIE2_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE3_X, COORD_ZOMBIE3_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE4_X, COORD_ZOMBIE4_Y) );

		zombies.add( new Zombie(COORD_ZOMBIE5_X, COORD_ZOMBIE5_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE6_X, COORD_ZOMBIE6_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE7_X, COORD_ZOMBIE7_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE8_X, COORD_ZOMBIE8_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE9_X, COORD_ZOMBIE9_Y) );

		zombies.add( new Zombie(COORD_ZOMBIE10_X, COORD_ZOMBIE10_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE11_X, COORD_ZOMBIE11_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE12_X, COORD_ZOMBIE12_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE13_X, COORD_ZOMBIE13_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE14_X, COORD_ZOMBIE14_Y) );

		zombies.add( new Zombie(COORD_ZOMBIE15_X, COORD_ZOMBIE15_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE16_X, COORD_ZOMBIE16_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE17_X, COORD_ZOMBIE17_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE18_X, COORD_ZOMBIE18_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE19_X, COORD_ZOMBIE19_Y) );

		zombies.add( new Zombie(COORD_ZOMBIE20_X, COORD_ZOMBIE20_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE21_X, COORD_ZOMBIE21_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE22_X, COORD_ZOMBIE22_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE23_X, COORD_ZOMBIE23_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE24_X, COORD_ZOMBIE24_Y) );

		zombies.add( new Zombie(COORD_ZOMBIE25_X, COORD_ZOMBIE25_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE26_X, COORD_ZOMBIE26_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE27_X, COORD_ZOMBIE27_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE28_X, COORD_ZOMBIE28_Y) );
		zombies.add( new Zombie(COORD_ZOMBIE29_X, COORD_ZOMBIE29_Y) );


		girders[0] = new Girder(COORD_GIRDER0_X, COORD_GIRDER0_Y);
		girders[1] = new Girder(COORD_GIRDER1_X, COORD_GIRDER1_Y);
		girders[2] = new Girder(COORD_GIRDER2_X, COORD_GIRDER2_Y);
		girders[3] = new Girder(COORD_GIRDER3_X, COORD_GIRDER3_Y);
		girders[4] = new Girder(COORD_GIRDER4_X, COORD_GIRDER4_Y);
		girders[5] = new Girder(COORD_GIRDER5_X, COORD_GIRDER5_Y);
		girders[6] = new Girder(COORD_GIRDER6_X, COORD_GIRDER6_Y);

		ladders[0] = new Ladder(COORD_LADDER0_X, COORD_LADDER0_Y);
		ladders[1] = new Ladder(COORD_LADDER1_X, COORD_LADDER1_Y);
		ladders[2] = new Ladder(COORD_LADDER2_X, COORD_LADDER2_Y);
		ladders[3] = new Ladder(COORD_LADDER3_X, COORD_LADDER3_Y);
		ladders[4] = new Ladder(COORD_LADDER4_X, COORD_LADDER4_Y);
		ladders[5] = new Ladder(COORD_LADDER5_X, COORD_LADDER5_Y);

		//-----------------------
		// END ADDING OF ELEMENTS
		//-----------------------


		floor = new Floor(COUNT_FLOOR);

		goal = new Goal(COORD_GOAL_X, COORD_GOAL_Y);

		abba = new ABBA();

		deadCounter = 0;

		// Resets the background offset to 0
		screen.resetBackground();

		// Play sound, if appropriate
		if(sound)
			music.loop();

	}

/*	Updates the game, moving elements and processing events */
//	----------------------------------------------------------
	private void runGame()
	{
		// If the player is alive, have him move
		if(player.isAlive())
			IKVZ.player.move();

		// If a bullet is onscreen, see if it hit a zombie. If offscreen, remove it from play
		int temp = -1;
		for(int i = 0; i < IKVZ.bullets.size(); i++)
		{
			if( ((Bullet)IKVZ.bullets.get(i)).onScreen() )
			{
				temp = ((Bullet)IKVZ.bullets.get(i)).onZombie();
				switch(temp)
				{
					case IKObject.NONE_HIT:
						((Bullet)IKVZ.bullets.get(i)).move();
						break;
					default:
						((Zombie)IKVZ.zombies.get(temp)).kill();
						IKVZ.zombies.remove(temp);
						IKVZ.bullets.remove(i);
						break;
				}
			}
			else
			{
				IKVZ.bullets.remove(i);
			}

		}

		// If a zombie is onscreen, move it
		for(int i = 0; i < IKVZ.zombies.size(); i++)
		{
			if( ((Zombie)IKVZ.zombies.get(i)).onScreen() )
			{
				((Zombie)IKVZ.zombies.get(i)).move();
			}
		}
	}

/**	Handles mouse events relating to continuing the game */
//	-------------------------------------------------------
	public void mousePressed(MouseEvent evt)
	{
		// Do nothing if the game is not in continue mode or ABBA was not clicked
		if( state != STATE_DEAD || !(abba.contains(evt.getX(), evt.getY() )) )
		{
			// If in developer mode, output coords of mouse click
			if(DEVELOPER)
				System.out.println("Place zombie @ x = "+(evt.getX() + amtShifted)+", y = "+( (evt.getY() > 200) ? 270 : 120));

			return;
		}

		if(sound)
			dancingQueen.play();

		// Heal player and return to play
		player.heal();
		state = STATE_RUN;

		// Do nothing while waiting on ABBA clip to play
		try
		{
			Thread.sleep(ABBA_TIME);
		}
		catch(InterruptedException e)
		{}

		if(sound)
			music.loop();

	}

/**	Does nothing */
//	---------------
	public void mouseReleased(MouseEvent evt)
	{

	}

/**	Does nothing */
//	---------------
	public void mouseClicked(MouseEvent evt)
	{

	}

/**	Does nothing */
//	---------------
	public void mouseEntered(MouseEvent evt)
	{

	}

/**	Does nothing */
//	---------------
	public void mouseExited(MouseEvent evt)
	{

	}

/**	Generates a random integer between <tt>min</tt> and <tt>max</tt> */
//	-------------------------------------------------------------------
	public static int random(int min, int max)
	{
		return((int)(Math.random()*(max-min+1)+min));
	}
}




class GameMenu extends JMenuBar
{
	public static JMenu 				mGame;
	public static JMenu 				mOptions;
	public static JMenu 				mHelp;

	public static JMenuItem 			miNewGame;
	public static JMenuItem 			miPause;
	public static JMenuItem 			miExit;

	public static JMenuItem				miControls;
	public static JMenu					mSound;
	public static JRadioButtonMenuItem	rbOn;
	public static JRadioButtonMenuItem	rbOff;

	public static JMenuItem 			miAbout;
	public static JMenuItem 			miHowToPlay;
	public static JMenuItem 			miCheats;

	public static String ARROWS_TEXT =			"Arrows";
	public static String NUMBERS_TEXT =			"Numbers";
	public static String LETTERS_TEXT =			"Letters";
	public static String CONTROLS_TEXT =		"MOVEMENT CONTROLS:\nARROWS - Right, Left, and Up Arrows\nNUMBERS - 6, 4, and 8\nLETTERS - D, A, and W\nSpace always fires.\n\nPlease select a scheme:";


	public GameMenu()
	{
		mGame = new JMenu("Game");
		mGame.setMnemonic(KeyEvent.VK_G);

		// BEGIN mGame ITEM SETUP
			miNewGame = new JMenuItem("New Game");
			miNewGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
			miNewGame.addActionListener( new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					IKVZ.state = IKVZ.STATE_NEW;
					miPause.setText("Pause");
				}
			});

			miPause = new JMenuItem("Pause");
			miPause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE, 0));
			miPause.addActionListener( new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					IKVZ.pauseToggle();
				}
			});

			miExit = new JMenuItem("Exit");
			miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
			miExit.addActionListener( new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					System.exit(0);
				}
			});


			mGame.add(miNewGame);
			mGame.add(miPause);
			mGame.add(miExit);
		// END mGAME ITEMS INITIALIZATION
		add(mGame);


		mOptions = new JMenu("Options");
		mOptions.setMnemonic(KeyEvent.VK_O);

		// BEGIN mOptions ITEMS INITIALIZATION
			miControls = new JMenuItem("Controls");
			miControls.addActionListener( new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					Object[] options = {ARROWS_TEXT,
					                    LETTERS_TEXT,
					                    NUMBERS_TEXT};
					int n = JOptionPane.showOptionDialog(null,
					    CONTROLS_TEXT,
					    "Controls",
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[0]);

					switch(n)
					{
						case 0:
							IKVZ.controls = IKVZ.CONTROLS_ARROWS;
							break;
						case 1:
							IKVZ.controls = IKVZ.CONTROLS_LETTERS;
							break;
						case 2:
							IKVZ.controls = IKVZ.CONTROLS_NUMBERS;
							break;
					}
				}
			});

			mSound = new JMenu("Sound");
			ButtonGroup group = new ButtonGroup();

			rbOn = new JRadioButtonMenuItem("On");
			rbOn.setSelected(true);
			rbOn.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent evt)
				{
					IKVZ.audioOn();
				}
			});

			rbOff = new JRadioButtonMenuItem("Off");
			rbOff.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent evt)
				{
					IKVZ.audioOff();
				}
			});

			group.add(rbOn);
			group.add(rbOff);
			mSound.add(rbOn);
			mSound.add(rbOff);

			mOptions.add(miControls);
			mOptions.add(mSound);

			add(mOptions);

			miAbout = new JMenuItem("About");
			miAbout.addActionListener( new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					JOptionPane.showMessageDialog(null, IKVZ.PROGRAM_ABOUT, IKVZ.PROGRAM_TITLE+" Info", JOptionPane.INFORMATION_MESSAGE);
				}
			});

			add(miAbout);

	}
}


class PlayPanel extends JPanel
{
	private static int playerCounter = 		0;
	private static int playerIncrement = 	0;
	public static Font fontStyle;
	public static FontMetrics fSM;
	public static String TITLE_TEXT =		"Go to Game/New Game to begin!";

	private int backgroundOffset =			0;

	public int getZeroWidth()
	{
		return IKVZ.BORDER_SIZE;
	}

	public int getZeroHeight()
	{
		return IKVZ.BORDER_SIZE;
	}

	public int getMaxWidth()
	{
		return getWidth() - IKVZ.BORDER_SIZE;
	}

	public int getMaxHeight()
	{
		return getHeight() - IKVZ.BORDER_SIZE;
	}

	public int getCenterY()
	{
		return getHeight() / 2;
	}

	public int getCenterX()
	{
		return getWidth() / 2;
	}

	private void renderTitle(Graphics g)
	{
		g.clearRect(getZeroWidth(), getZeroHeight(), getMaxWidth(), getMaxHeight());
		g.setColor(Color.WHITE);
		g.fillRect(getZeroWidth(), getZeroHeight(), getMaxWidth(), getMaxHeight() );
		g.drawImage(IKVZ.logoImage,
					getCenterX() - (IKVZ.logoImage.getWidth(this) / 2),
					getCenterY() - (IKVZ.logoImage.getHeight(this) / 2),
					this);
		g.setColor(Color.BLACK);
		g.drawString(	TITLE_TEXT,
						getCenterX() - (fSM.stringWidth(TITLE_TEXT) / 2),
						getCenterY() + (IKVZ.logoImage.getHeight(this) / 2) + fSM.getHeight());

	}

	private void renderGame(Graphics g)
	{

		if(playerCounter++ > IKVZ.DELAY_ANIMATION && IKVZ.player.isMoving() )
		{
			playerCounter = 0;
			playerIncrement = (playerIncrement == 0) ? IKVZ.IMAGE_PDIRECTIONS: 0;
		}


		g.drawImage(IKVZ.backgroundImage,
					getZeroWidth() + backgroundOffset,
					getZeroHeight(),
					this);


		for(int i = 0; i < IKVZ.COUNT_LADDER; i++)
		{
			if(IKVZ.ladders[i].getX() + IKVZ.ladders[i].getWidth() < getZeroWidth() )
			{
				continue;
			}
			else if(IKVZ.ladders[i].getX() > getMaxWidth() )
			{
				break;
			}

			g.drawImage(IKVZ.ladderImage,
						IKVZ.ladders[i].getX(),
						IKVZ.ladders[i].getY(),
						this);
		}


		for(int i = 0; i < IKVZ.COUNT_GIRDER; i++)
		{
			if(IKVZ.girders[i].getX() + IKVZ.girders[i].getWidth() < getZeroWidth() )
			{
				continue;
			}
			else if(IKVZ.girders[i].getX() > getMaxWidth() )
			{
				break;
			}

			g.drawImage(IKVZ.girderImage,
						IKVZ.girders[i].getX(),
						IKVZ.girders[i].getY(),
						this);
		}



		g.drawImage( (IKVZ.player.isAlive())
							? IKVZ.playerImage[IKVZ.player.getDirection() + playerIncrement]
							: IKVZ.deadPlayerImage,
					IKVZ.player.getX(),
					IKVZ.player.getY(),
					this);


		for(int i = 0; i < IKVZ.zombies.size(); i++)
		{
			if( ((Zombie)IKVZ.zombies.get(i)).getX() + ((Zombie)IKVZ.zombies.get(i)).getWidth() < getZeroWidth() )
			{
				continue;
			}
			else if( ((Zombie)IKVZ.zombies.get(i)).getX() > getMaxWidth() )
			{
				break;
			}

			g.drawImage(IKVZ.zombieImage[((Zombie)IKVZ.zombies.get(i)).getDirection()],
						((Zombie)IKVZ.zombies.get(i)).getX(),
						((Zombie)IKVZ.zombies.get(i)).getY(),
						this);
		}


		for(int i = 0; i < IKVZ.deadZombies.size(); i++)
		{
			if( ((Zombie)IKVZ.deadZombies.get(i)).getX() + ((Zombie)IKVZ.deadZombies.get(i)).getWidth() < getZeroWidth() )
			{
				continue;
			}
			else if( ((Zombie)IKVZ.deadZombies.get(i)).getX() > getMaxWidth() )
			{
				break;
			}

			g.drawImage(IKVZ.deadZombieImage,
						((Zombie)IKVZ.deadZombies.get(i)).getX(),
						((Zombie)IKVZ.deadZombies.get(i)).getY(),
						this);
		}


		g.setColor(Color.WHITE);

		for(int i = 0; i < IKVZ.bullets.size(); i++)
		{
			if( ((Bullet)IKVZ.bullets.get(i)).getX() + ((Bullet)IKVZ.bullets.get(i)).getWidth() < getZeroWidth() )
			{
				continue;
			}
			else if( ((Bullet)IKVZ.bullets.get(i)).getX() > getMaxWidth() )
			{
				break;
			}

			g.fillRect(	((Bullet)IKVZ.bullets.get(i)).getX(),
						((Bullet)IKVZ.bullets.get(i)).getY(),
						IKVZ.BULLET_WIDTH,
						IKVZ.BULLET_HEIGHT);
		}

		if(IKVZ.goal.onScreen() )
		{
			g.drawImage(IKVZ.goalImage,
						IKVZ.goal.getX(),
						IKVZ.goal.getY(),
						this);
		}


		for(int i = 0; i < IKVZ.COUNT_FLOOR; i++)
		{
			g.drawImage(IKVZ.girderImage,
						IKVZ.floor.getX(i),
						IKVZ.floor.getY(i),
						this);
		}

		g.setColor(Color.WHITE);

		g.drawString(	"Health:",
						getZeroWidth() + IKVZ.HEALTH_TEXT_X,
						getZeroHeight() + fSM.getMaxAscent() - fSM.getMaxDescent() + IKVZ.HEALTH_TEXT_Y);

		g.fillRect(		getZeroWidth() + 2 * IKVZ.HEALTH_TEXT_X + fSM.stringWidth("Health:"),
						getZeroHeight() + IKVZ.HEALTH_TEXT_Y,
						IKVZ.HEALTH_BAR_LENGTH,
						fSM.getAscent() );

		g.setColor(Color.RED);
		g.fillRect(		getZeroWidth() + 2 * IKVZ.HEALTH_TEXT_X + fSM.stringWidth("Health:") + IKVZ.HEALTH_BAR_BORDER,
						getZeroHeight() + IKVZ.HEALTH_TEXT_Y + IKVZ.HEALTH_BAR_BORDER,
						(int)(IKVZ.player.getHealthPer() * (IKVZ.HEALTH_BAR_LENGTH - 2 * IKVZ.HEALTH_BAR_BORDER)),
						fSM.getAscent() - 2 * IKVZ.HEALTH_BAR_BORDER );


	}

	private void renderPause(Graphics g)
	{
		g.setColor(Color.RED);
		g.drawString(	"Pause",
						getCenterX() - (fSM.stringWidth("Pause") / 2),
						getCenterY() - (fSM.getHeight() / 2));
	}

	private void renderDead(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(getZeroWidth(), getZeroHeight(), getMaxWidth(), getMaxHeight() );
		g.setColor(Color.WHITE);
		g.drawString(	"Click on ABBA to continue!!!",
						getCenterX() - (fSM.stringWidth("Click on ABBA to continue!!!") / 2),
						getMaxHeight() - fSM.getHeight());

		g.drawImage(IKVZ.abbaImage,
					IKVZ.abba.getX(),
					IKVZ.abba.getY(),
					this);
	}

	private void renderError(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.fillRect(getZeroWidth(), getZeroHeight(), getMaxWidth(), getMaxHeight() );
		g.setColor(Color.BLACK);
		g.drawString(	"There is a serious error!",
						getCenterX() - (fSM.stringWidth("There is a serious error!") / 2),
						getCenterY() - (fSM.getHeight() / 2));
	}

	public PlayPanel()
	{
		setBorder(BorderFactory.createMatteBorder(IKVZ.BORDER_SIZE, IKVZ.BORDER_SIZE, IKVZ.BORDER_SIZE, IKVZ.BORDER_SIZE, Color.black));;
		fontStyle = new Font("Serif", Font.BOLD, 16);
	}

	public void paintComponent(Graphics g)
	{

		super.paintComponent(g);
		g.setFont(fontStyle);
		fSM = g.getFontMetrics(fontStyle);
		switch(IKVZ.state)
		{
			case IKVZ.STATE_TITLE:
				renderTitle(g);
				break;
			case IKVZ.STATE_PAUSE:
				renderGame(g);
				renderPause(g);
				break;
			case IKVZ.STATE_NEW:
				break;
			case IKVZ.STATE_RUN:
				renderGame(g);
				break;
			case IKVZ.STATE_DEAD:
				renderDead(g);
				break;
			default:
				renderError(g);
				break;
		}
	}

	public void shiftBackgroundBy(int amount)
	{
		backgroundOffset += amount;
		if(backgroundOffset + IKVZ.backgroundImage.getWidth(this) <= getMaxWidth() )
		{
			IKVZ.state = IKVZ.STATE_TITLE;
		}
	}

	public void resetBackground()
	{
		backgroundOffset = 0;
	}

}

abstract class IKObject
{
	private int x;
	private int y;
	public static final int NONE_HIT =		-1;

	public IKObject(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	abstract public int getHeight();
	abstract public int getWidth();


	public boolean intersects(IKObject target)
	{
		Rectangle rect = new Rectangle(x, y, getWidth(), getHeight() );
		return rect.intersects( new Rectangle(	target.getX(),
												target.getY(),
												target.getWidth(),
												target.getHeight() ) );
	}

	public void setAt(int x, int y)
	{
		if(x != 0)
			this.x = x;
		if(y != 0)
			this.y = y;
	}

	public void shiftBy(int x, int y)
	{
		this.x += x;
		this.y += y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int distToFloor()
	{
		return (IKVZ.screen.getMaxHeight() - IKVZ.floor.getHeight()) - (getY() + getHeight());
	}

	public int distToGirder()
	{

		for(int i = 0; i < IKVZ.COUNT_GIRDER; i++)
		{

			if(	(getX() + getWidth() > IKVZ.girders[i].getX() &&
				getX() + getWidth() < IKVZ.girders[i].getX() + IKVZ.girders[i].getWidth() )
				||
				(getX() < IKVZ.girders[i].getX() + IKVZ.girders[i].getWidth() &&
					getX() > IKVZ.girders[i].getX()) )
			{
				return IKVZ.girders[i].getY() - (getY() + getHeight());
			}
		}

		return -1;
	}

	public boolean onScreen()
	{
		return getX() + getWidth() > IKVZ.screen.getZeroWidth() && getX() < IKVZ.screen.getMaxWidth();
	}

	public boolean contains(int x, int y)
	{
		return (new Rectangle(x, y, getWidth(), getHeight() )).contains( x, y);
	}

	public boolean onLadder()
	{
		for(int i = 0; i < IKVZ.COUNT_LADDER; i++)
		{
			if(IKVZ.ladders[i].intersects(this) )
			{
				return true;
			}
		}
		return false;
	}

	public int onZombie()
	{
		for(int i = 0; i < IKVZ.zombies.size(); i++)
		{
			if( ((Zombie)IKVZ.zombies.get(i)).intersects(this) )
			{
				return i;
			}
		}
		return NONE_HIT;

	}

}

interface Moves
{
	public void move();
	public void setDirection(byte direction);
	public byte getDirection();
	public boolean isMoving();

}

class Player extends IKObject implements Moves
{
	private int health;
	private int distToFall;
	private int distgirder;
	private byte direction;
	private boolean moving;
	private static int width;
	private static int height;

	public Player(int x, int y)
	{
		super(x,y);
		moving = false;
		health = IKVZ.HEALTH_AMOUNT;
	}

	public void hit()
	{
		health -=  IKVZ.HEALTH_DAMAGE;
		if(health < 0)
			health = 0;
	}

	public boolean isAlive()
	{
		return health > 0;
	}

	public double getHealthPer()
	{
		return (double)health / (double)IKVZ.HEALTH_AMOUNT;
	}

	public void heal()
	{
		health = IKVZ.HEALTH_AMOUNT;
	}

	public void move()
	{

		if(onZombie() != IKObject.NONE_HIT )
			hit();

		distToFall = distToGirder();

		if(distToFall < 0)
		{
			distToFall = distToFloor();
		}


		if( !(distToFall == 0 || onLadder() ))
		{
			shiftBy(0, (distToFall > IKVZ.SHIFT_AMOUNT) ? IKVZ.SHIFT_AMOUNT : distToFall);
			moving = false;
			return;
		}

		if(!moving)
			return;


		switch(direction)
		{
			case IKVZ.DIR_RIGHT:
				if( getX() > IKVZ.screen.getMaxWidth() * (IKVZ.MOVE_RATIO) )
				{
					IKVZ.shiftAll(0 - IKVZ.SHIFT_AMOUNT);
				}
				else
				{
					shiftBy(IKVZ.SHIFT_AMOUNT, 0);
				}
				break;

			case IKVZ.DIR_LEFT:
				if( getX() <= IKVZ.screen.getZeroWidth())
				{
					setDirection(IKVZ.DIR_STOP);
				}
				else
				{
					shiftBy( 0 - IKVZ.SHIFT_AMOUNT, 0);
				}
				break;

			case IKVZ.DIR_UP:
				if(onLadder())
				{
					shiftBy(0, 0 - IKVZ.SHIFT_AMOUNT);
				}
				else
					setDirection(IKVZ.DIR_STOP);

				break;
		}

	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public static void setDimensionsTo(Image image)
	{
		height = image.getHeight(IKVZ.get());
		width = image.getWidth(IKVZ.get());
	}

	public static void setDimensionsTo(int newWidth, int newHeight)
	{
		width = newWidth;
		height = newHeight;
	}

	public void fire()
	{
		switch(direction)
		{
			case IKVZ.DIR_UP:
			case IKVZ.DIR_RIGHT:
				IKVZ.bullets.add( new Bullet(	getX() + getWidth(),
												getY() + IKVZ.BULLET_FIRE_FROM_Y,
												IKVZ.DIR_RIGHT ));
				break;
			case IKVZ.DIR_LEFT:
				IKVZ.bullets.add( new Bullet(	getX() - IKVZ.BULLET_WIDTH,
												getY() + IKVZ.BULLET_FIRE_FROM_Y,
												IKVZ.DIR_LEFT ));
				break;
		}
	}

	public void setDirection(byte direction)
	{
		switch(direction)
		{
			case IKVZ.DIR_RIGHT:
			case IKVZ.DIR_LEFT:
			case IKVZ.DIR_UP:
				this.direction = direction;
				moving = true;
				break;
			default:
				moving = false;
				break;
		}
	}

	public byte getDirection()
	{
		return direction;
	}

	public boolean isMoving()
	{
		return moving;
	}

}

class Zombie extends IKObject implements Moves
{
	private byte direction;
	private int moveCount;
	private static int speed = 2;
	private static int delay = 15;
	private boolean alive;
	private boolean moving;
	private static int width;
	private static int height;

	public Zombie(int x, int y)
	{
		super(x, y);
		alive = true;
		moving = false;
		direction = IKVZ.DIR_LEFT;
		moveCount = 0;
	}

	public void move()
	{
		boolean onGirder = false;;

		if(moveCount++ > delay && Math.random() < IKVZ.ZDIR_CHANGE_ODDS)
		{
			double temp = Math.random();
			if(temp < .4)
			{
				setDirection(IKVZ.DIR_RIGHT);
			}
			else if(temp < .8)
			{
				setDirection(IKVZ.DIR_LEFT);
			}
			else
				setDirection(IKVZ.DIR_STOP);;

			moveCount = 0;
		}

		if(!moving)
		{
			return;
		}

		if(distToGirder() >= 0 )
		{
			onGirder = true;
		}

		switch(direction)
		{
			case IKVZ.DIR_RIGHT:
				shiftBy(speed, 0);
				if(onGirder && distToGirder() < 0)
				{
					setDirection(IKVZ.DIR_LEFT);
					shiftBy(0 - speed, 0);
				}
				break;
			case IKVZ.DIR_LEFT:
				shiftBy(0 - speed, 0);
				if(onGirder && distToGirder() < 0)
				{
					setDirection(IKVZ.DIR_RIGHT);
					shiftBy(speed, 0);
				}
				break;
		}

	}

	public static void setDimensionsTo(Image image)
	{
		height = image.getHeight(IKVZ.get());
		width = image.getWidth(IKVZ.get());
	}

	public static void setDimensionsTo(int newWidth, int newHeight)
	{
		width = newWidth;
		height = newHeight;
	}

	public byte getDirection()
	{
		return direction;
	}

	public void kill()
	{
		alive = false;
		IKVZ.deadZombies.add(this);
	}

	public void setDirection(byte direction)
	{
		switch(direction)
		{
			case IKVZ.DIR_RIGHT:
			case IKVZ.DIR_LEFT:
				this.direction = direction;
				moving = true;
				break;
			default:
				moving = false;
				break;
		}
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

	public boolean isMoving()
	{
		return moving;
	}

}

class Bullet extends IKObject implements Moves
{
	private byte direction;
	private static int speed = 20;
	private boolean moving;

	private static int height;
	private static int width;

	public Bullet(int x, int y, byte direction)
	{
		super(x,y);
		moving = true;
		switch(direction)
		{
			case IKVZ.DIR_LEFT:
			case IKVZ.DIR_RIGHT:
				this.direction = direction;
				break;
		}
	}

	public void move()
	{
		if(!moving)
			return;


		switch(direction)
		{
			case IKVZ.DIR_RIGHT:
				shiftBy(speed, 0);
				break;

			case IKVZ.DIR_LEFT:
				shiftBy( 0 - speed, 0);
				break;
		}

	}

	public byte getDirection()
	{
		return direction;
	}

	public void setDirection(byte direction)
	{
		switch(direction)
		{
			case IKVZ.DIR_LEFT:
			case IKVZ.DIR_RIGHT:
				this.direction = direction;
				break;
		}

	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

	public boolean isMoving()
	{
		return moving;
	}

	public static void setDimensionsTo(Image image)
	{
		height = image.getHeight(IKVZ.get());
		width = image.getWidth(IKVZ.get());
	}

	public static void setDimensionsTo(int newWidth, int newHeight)
	{
		width = newWidth;
		height = newHeight;
	}
}

class Girder extends IKObject
{
	private static int height;
	private static int width;

	public Girder(int x, int y)
	{
		super(x,y);
	}

	public static void setDimensionsTo(Image image)
	{
		height = image.getHeight(IKVZ.get());
		width = image.getWidth(IKVZ.get());
	}

	public static void setDimensionsTo(int newWidth, int newHeight)
	{
		width = newWidth;
		height = newHeight;
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

}

class Floor
{
	private Girder girders[];
	private byte order[];

	public Floor(int count)
	{
		order = new byte[count];
		girders = new Girder[count];
		for(byte i = 0; i < count; i++)
		{
			order[i] = i;
			girders[i] = new Girder(	IKVZ.screen.getZeroWidth() + i * IKVZ.girderImage.getWidth(IKVZ.get()),
										IKVZ.screen.getMaxHeight() - IKVZ.girderImage.getHeight(IKVZ.get()) );
		}

	}

	public int getX(int pos)
	{
		return girders[order[pos]].getX();
	}

	public int getY(int pos)
	{
		return girders[order[pos]].getY();
	}

	public int getHeight()
	{
		return IKVZ.girderImage.getHeight(IKVZ.get());
	}

	public void shiftBy(int amount)
	{
		byte temp;
		for(byte i = 0; i < girders.length; i++)
		{
			girders[order[i]].shiftBy(amount, 0);
			if( i == 0 )
			{
				if(girders[order[i]].getX() + girders[order[i]].getWidth() < IKVZ.screen.getZeroWidth() )
				{
					temp = order[i];
					for(int j = 0; j < order.length - 1; j++)
					{
						order[j] = order[j + 1];
					}
					order[order.length - 1] = temp;

					girders[order[i]].shiftBy(amount, 0);

					girders[order[order.length - 1]].setAt(	girders[order[order.length - 2]].getX() + girders[order[order.length - 2]].getWidth(),
															0);
				}
				else if(girders[order[i]].getX() > IKVZ.screen.getZeroWidth() )
				{
					temp = order[order.length - 1];
					for(int j = order.length - 1; j > 0; j--)
					{
						order[j] = order[j - 1];
					}
					order[0] = temp;

					i++;

					girders[order[0]].setAt(girders[order[1]].getX() - girders[order[1]].getWidth(),
											0);

				}
			}
		}
	}
}


class Ladder extends IKObject
{
	private static int height;
	private static int width;

	public Ladder(int x, int y)
	{
		super(x,y);
	}

	public static void setDimensionsTo(Image image)
	{
		height = image.getHeight(IKVZ.get());
		width = image.getWidth(IKVZ.get());
	}

	public static void setDimensionsTo(int newWidth, int newHeight)
	{
		width = newWidth;
		height = newHeight;
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}
}

class Goal extends IKObject
{
	private static int height;
	private static int width;

	public Goal(int x, int y)
	{
		super(x,y);
	}

	public static void setDimensionsTo(Image image)
	{
		height = image.getHeight(IKVZ.get());
		width = image.getWidth(IKVZ.get());
	}

	public static void setDimensionsTo(int newWidth, int newHeight)
	{
		width = newWidth;
		height = newHeight;
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}
}

class ABBA extends IKObject implements Moves
{
	private static int speed = 10;
	private int xSign = 1;
	private int ySign = 1;

	private static int height;
	private static int width;

	public ABBA()
	{
		super(0,0);
		setAt(	IKVZ.random(IKVZ.screen.getZeroWidth(), IKVZ.screen.getMaxWidth() - getWidth()),
				IKVZ.random(IKVZ.screen.getZeroHeight(), IKVZ.screen.getMaxHeight() - getHeight()));
	}

	public void move()
	{
		shiftBy(xSign * speed, ySign * speed);
		if(getX() < IKVZ.screen.getZeroWidth() || getX() + getWidth() > IKVZ.screen.getMaxWidth() )
			xSign = 0 - xSign;

		if(getY() < IKVZ.screen.getZeroHeight() || getY() + getHeight() > IKVZ.screen.getMaxHeight() )
			ySign = 0 - ySign;
	}

	public void setDirection(byte direction)
	{}

	public byte getDirection()
	{
		return -1;
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

	public boolean isMoving()
	{
		return true;
	}

	public static void setDimensionsTo(Image image)
	{
		height = image.getHeight(IKVZ.get());
		width = image.getWidth(IKVZ.get());
	}

	public static void setDimensionsTo(int newWidth, int newHeight)
	{
		width = newWidth;
		height = newHeight;
	}
}
