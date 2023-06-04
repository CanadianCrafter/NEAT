package snake;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;


/**
 * This class allows user to draw a digit which the program identifies.
 * @author Bryan Wang
 * Reference:https://math.hws.edu/eck/cs124/s14/lab7/lab7-files/Grid.java
 *
 */
public class GameGUI extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
	private final static int BOARD_WIDTH = 20;
	private final static int BOARD_HEIGHT = 20;
	private final static int MARGIN = 4;
	private final static int BOX_SIZE = 40;
	
	private static JFrame window; 
	
	private Color[][] gridColour; //the colour of grid[row][column]; if null, it it is transparent
	private Color lineColour = Color.WHITE; // Colour of grid lines; if null, no lines are drawn.
	private Color backgroundColour = new Color(227,227,227); // Colour of grid lines; if null, no lines are drawn.
	private Color cellColour = Color.PINK; //Colour of an alive cell
	
	private final static int fastSpeed = 50;
	private final static int normalSpeed = 100;
	private final static int slowSpeed = 250;
	private Timer animationTimer = new Timer(normalSpeed, this); // the stop motion animation runs at 10 fps or every 0.1 seconds
	private int animationIndex = 0; //timing for the animations
	private final int NUM_ITERATIONS = 10000;
	
	//menubar
	private static JMenuBar mb = new JMenuBar();
	private static JMenu menu = new JMenu();
	private static JMenuItem wipe;
	private static JMenuItem next;
	private static JMenuItem play;
	private static JMenuItem pause;
	private static JMenuItem reset;
	private static JMenuItem exit;
	private static JMenu speed = new JMenu();
	private static JMenuItem fast;
	private static JMenuItem normal;
	private static JMenuItem slow;
	
//	private final int brushRadius = 11; //in pixels
	
	public static Grid grid;

	
	/**
	 * Creates a panel with a specified number of rows and columns of squares of a certain size.
	 * @param rows  The number of rows of squares.
	 * @param columns  The number of columns of squares.
	 * @param preferredSquareSize  The desired size, in pixels, for the squares. This will
	 *     be used to compute the preferred size of the panel. 
	 */
	public GameGUI() {
		menuBar();
		gridColour = new Color[BOARD_HEIGHT][BOARD_WIDTH]; // Create the array that stores square colors.
		setPreferredSize(new Dimension(BOX_SIZE*BOARD_WIDTH, BOX_SIZE*BOARD_HEIGHT));
		setBackground(backgroundColour); // Set the background color for this panel.
		addMouseMotionListener(this);     // Mouse actions will call methods in this object.
		addMouseListener(this);     // Mouse actions will call methods in this object.
	}
	
	/**
	 * This creates a window and sets its content to be a panel of type Grid.
	 * @param args
	 */
	public static void main(String[] args) {
		grid = new Grid();
		//GUI
		window = new JFrame("Conway's Game of Life");  // Create a window and names it.
		GameGUI content = new GameGUI();  // 100 by 100 grid of 8px x 8px squares
		window.setContentPane(content);  // Add the Grid panel to the window.
		window.pack(); // Set the size of the window based on the panel's preferred size.
		Dimension screenSize; // A simple object containing the screen's width and height.
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// position for top left corner of the window
		int left = (screenSize.width - window.getWidth()) / 2;
		int top = (screenSize.height - window.getHeight()) / 2;
		window.setLocation(left,top);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		
	}
	
	/**
	 * Sets up the menubar
	 */
	private void menuBar() {
		mb = new JMenuBar();
		menu = new JMenu("Menu");
		
		speed = new JMenu("Speed");

		// menu items
		wipe = new JMenuItem("Reset Canvas");
		next = new JMenuItem("Next Step");
		play = new JMenuItem("Play");
		pause = new JMenuItem("Pause");
		reset = new JMenuItem("Reset to Original");
		exit = new JMenuItem("Exit");
		
		fast = new JMenuItem("Fast");
		normal = new JMenuItem("Normal");
		slow = new JMenuItem("Slow");

		// add to action listener for the menu items
		wipe.addActionListener(this);
		next.addActionListener(this);
		play.addActionListener(this);
		pause.addActionListener(this);
		reset.addActionListener(this);
		exit.addActionListener(this);
		
		fast.addActionListener(this);
		normal.addActionListener(this);
		slow.addActionListener(this);

		window.setJMenuBar(mb); // add menu bar
		mb.add(menu); // add menu to menubar
		mb.add(speed);
		menu.add(wipe); // add items
		menu.add(next);
		menu.add(play);
		menu.add(pause);
		menu.add(reset);
		menu.add(exit);
		
		speed.add(fast);
		speed.add(normal);
		speed.add(slow);

	}
	
	/**
	 * Finds the row numbers for grid squares within brushRadius many pixels from a y-coordinate
	 * @param pixelY a pixel y-coordinate. 
	 * @return The row numbers brushRadius away from pixelY. 
	 */
	private ArrayList<Integer> findRows(int pixelY) {
		ArrayList<Integer> rows = new ArrayList<Integer>();
		for(int i = (int) Math.round(((double)pixelY)/getHeight()*BOARD_HEIGHT); 
				i<= Math.round(((double)pixelY)/getHeight()*BOARD_HEIGHT);i++) {
			rows.add(i);
		}
		return rows;
	}
	
	/**
	 * Finds the column numbers for grid squares within brushRadius many pixels from a x-coordinate
	 * @param pixelX a pixel x-coordinate. 
	 * @return The column numbers corresponding to pixelY. 
	 */
	private ArrayList<Integer> findColumns(int pixelX) {
		ArrayList<Integer> cols = new ArrayList<Integer>();
		for(int i = (int) Math.round(((double)pixelX)/getHeight()*BOARD_WIDTH); 
				i<= Math.round(((double)pixelX)/getHeight()*BOARD_WIDTH);i++) {
			cols.add(i);
		}
		return cols;
	}
 
	private void updateGrid() {
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			for (int col = 0; col < BOARD_WIDTH; col++) {
				if(grid.gameBoard[MARGIN+row][MARGIN+col]) {
					gridColour[row][col] = cellColour;
				}
				else {
					gridColour[row][col] = null;
				}
			}
		}
	}

	/**
	 * Draws the grid of squares and grid lines (if the colour isn't null).
	 */
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0,0,getWidth(),getHeight());
		double cellWidth = (double)getWidth() / BOARD_WIDTH;
		double cellHeight = (double)getHeight() / BOARD_HEIGHT;
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			for (int col = 0; col < BOARD_WIDTH; col++) {
				if (gridColour[row][col] != null) {
					int x1 = (int)(col*cellWidth);
					int y1 = (int)(row*cellHeight);
					int x2 = (int)((col+1)*cellWidth);
					int y2 = (int)((row+1)*cellHeight);
					g.setColor(gridColour[row][col]);
					g.fillRect( x1, y1, (x2-x1), (y2-y1) );
				}
			}
		}
		if (lineColour != null) {
			g.setColor(lineColour);
			for (int row = 1; row < BOARD_HEIGHT; row++) {
				int y = (int)(row*cellHeight);
				g.drawLine(0,y,getWidth(),y);
			}
			for (int col = 1; col < BOARD_HEIGHT; col++) {
				int x = (int)(col*cellWidth);
				g.drawLine(x,0,x,getHeight());
			}
		}
	}
	
	/**
	 * Turns the grid squares where the user clicks (and holds) black.
	 */
	@Override
	public void mouseDragged(MouseEvent evt) {
		// the rows and columns in the grid of squares where the user clicked.
		ArrayList<Integer> rows = findRows(evt.getY() );
		ArrayList<Integer> cols = findColumns(evt.getX());
		for(int row = 0; row <rows.size();row++) {
			for(int col = 0; col < cols.size();col++) {
				if(gridColour[rows.get(row)][cols.get(col)]==null) {
					gridColour[rows.get(row)][cols.get(col)] = cellColour;
				}
				else {
					gridColour[rows.get(row)][cols.get(col)] = null;
				}
				
				grid.gameBoard[MARGIN+rows.get(row)][MARGIN+cols.get(col)] = !grid.gameBoard[MARGIN+rows.get(row)][MARGIN+cols.get(col)];
			}
		}
		
		repaint(); // Causes the panel to be redrawn, by calling the paintComponent method.
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent evt) {
		// the rows and columns in the grid of squares where the user clicked.
		ArrayList<Integer> rows = findRows(evt.getY() );
		ArrayList<Integer> cols = findColumns(evt.getX());
		for(int row = 0; row <rows.size();row++) {
			for(int col = 0; col < cols.size();col++) {
				if(gridColour[rows.get(row)][cols.get(col)]==null) {
					gridColour[rows.get(row)][cols.get(col)] = cellColour;
				}
				else {
					gridColour[rows.get(row)][cols.get(col)] = null;
				}
				
				grid.gameBoard[MARGIN+rows.get(row)][MARGIN+cols.get(col)] = !grid.gameBoard[MARGIN+rows.get(row)][MARGIN+cols.get(col)];
				System.out.println(rows.get(row) + " " + cols.get(col));
			}
		}
		repaint(); // Causes the panel to be redrawn, by calling the paintComponent method.
	}

	@Override
	public void mousePressed(MouseEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		// resets the canvas
		if (event.getSource() == wipe) {
//			grid.wipeGrid();
			for(int row = 0; row < BOARD_HEIGHT; row++) {
				for(int col = 0; col < BOARD_WIDTH; col++) {
					gridColour[row][col] = null;
				}
			}
			animationTimer.stop();
			updateGrid();
			repaint();
		}
		// recognizes the digit
		else if (event.getSource() == next) {
			grid.updateGrid();
			updateGrid();
			repaint();
		}
		//plays the animation 500 iterations
		else if (event.getSource() == play) {
			animationTimer.start(); // starts timer for the animation
			animationIndex = 0;
//			grid.saveGrid();
		}
		//stops the animation
		else if (event.getSource() == pause) {
			animationTimer.stop(); // stops timer for the animation
		}
		//stops the animation
		else if (event.getSource() == reset) {
			animationTimer.stop(); // stops timer for the animation
//			grid.resetGrid();
			updateGrid();
			repaint();
		}
		// exits 
		else if (event.getSource() == exit) {
			System.exit(0);
		}
		//fast speed
		else if (event.getSource() == fast) {
			animationTimer.setDelay(fastSpeed);
		}
		else if (event.getSource() == normal) {
			animationTimer.setDelay(normalSpeed);
		}
		else if (event.getSource() == slow) {
			animationTimer.setDelay(slowSpeed);
		}
		
		else if (event.getSource() == animationTimer) {
			// each 1/10 second, update the board
			if(animationIndex<NUM_ITERATIONS) {
				grid.updateGrid();
				updateGrid();
				repaint();
			}
			
			//stops animation
			else {
				animationTimer.stop();
				animationIndex = 0;
			}
			animationIndex++;
				
		}
		
	}

	
} 