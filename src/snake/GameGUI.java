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
public class GameGUI extends JPanel implements ActionListener, KeyListener{
	private final static int BOX_SIZE = 40;
	
	private static JFrame window; 
	
	private Color[][] gridColour; //the colour of grid[row][column]; if null, it it is transparent
	private Color lineColour = Color.WHITE; 
	private Color snakeColour = Color.CYAN; // Colour of grid lines; if null, no lines are drawn.
	private Color snakeHeadColour = Color.BLUE; 
	private Color backgroundColour = Color.GRAY; 
	private Color appleColour = Color.RED; 
	
	private final static int speed = 200;
	private Timer animationTimer = new Timer(speed, this); // the stop motion animation runs at 10 fps or every 0.1 seconds
	private int animationIndex = 0; //timing for the animations
	private final int NUM_ITERATIONS = 10000;
	
	//menubar
	private static JMenuBar mb = new JMenuBar();
	private static JMenu menu = new JMenu();
	private static JMenuItem restart;
	private static JMenuItem exit;
	
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
		gridColour = new Color[Grid.getBoardHeight()][Grid.getBoardWidth()]; // Create the array that stores square colors.
		setPreferredSize(new Dimension(BOX_SIZE*Grid.getBoardWidth(), BOX_SIZE*Grid.getBoardHeight()));
		setBackground(backgroundColour); // Set the background color for this panel.
		
		animationTimer.start(); // starts timer for the animation
		animationIndex = 0;
		window.addKeyListener(this);
	}
	
	/**
	 * This creates a window and sets its content to be a panel of type Grid.
	 * @param args
	 */
	public static void main(String[] args) {
		grid = new Grid();
		//GUI
		window = new JFrame("Snake");  // Create a window and names it.
		GameGUI content = new GameGUI();  // 10 by 10 grid of 40px x 40px squares
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
		
		// menu items
		restart = new JMenuItem("Restart");
		exit = new JMenuItem("Exit");
		

		// add to action listener for the menu items
		restart.addActionListener(this);
		exit.addActionListener(this);
		
		

		window.setJMenuBar(mb); // add menu bar
		mb.add(menu); // add menu to menubar
		menu.add(restart);
		menu.add(exit);

	}
	
 
	private void updateGrid() {
		
		Grid.updateGrid();
		String gameState = Grid.getGameState();
		if(gameState.equals("win")) {
			animationTimer.stop();
			System.out.println("Win!");
			System.out.printf("Score: %d", Grid.getScore());
			return;
		}
		else if(gameState.equals("lose")) {
			animationTimer.stop();
			System.out.println("Lose!");
			System.out.printf("Score: %d", Grid.getScore());
			return;
		}
		
		
		for (int row = 0; row < Grid.getBoardHeight(); row++) {
			for (int col = 0; col < Grid.getBoardWidth(); col++) {
				if(grid.gameBoard[row][col]) {
					gridColour[row][col] = snakeColour;
				}
				else {
					gridColour[row][col] = null;
				}
			}
		}
		
		Coords apple = Grid.getApple();
		Coords snakeHead = Grid.getSnakeHead();
		
		gridColour[apple.getRow()][apple.getCol()] = appleColour;
		gridColour[snakeHead.getRow()][snakeHead.getCol()] = snakeHeadColour;
	}

	/**
	 * Draws the grid of squares and grid lines (if the colour isn't null).
	 */
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0,0,getWidth(),getHeight());
		double cellWidth = (double)getWidth() / Grid.getBoardWidth();
		double cellHeight = (double)getHeight() / Grid.getBoardHeight();
		for (int row = 0; row < Grid.getBoardHeight(); row++) {
			for (int col = 0; col < Grid.getBoardWidth(); col++) {
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
			for (int row = 1; row < Grid.getBoardHeight(); row++) {
				int y = (int)(row*cellHeight);
				g.drawLine(0,y,getWidth(),y);
			}
			for (int col = 1; col < Grid.getBoardHeight(); col++) {
				int x = (int)(col*cellWidth);
				g.drawLine(x,0,x,getHeight());
			}
		}
	}
		
	
	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == animationTimer) {
			// each 1/10 second, update the board
			if(animationIndex<NUM_ITERATIONS) {
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
		//restarts game
		else if (event.getSource() == restart) {
			Grid.initialize();
			updateGrid();
			animationTimer.start(); // starts timer for the animation
			animationIndex = 0;		
			updateGrid();
		}
		// exits 
		else if (event.getSource() == exit) {
			System.exit(0);
		}
		
	}

	@Override
	public void keyTyped(KeyEvent key) {
	
	}

	@Override
	public void keyPressed(KeyEvent key) {
		if (key.getKeyCode() == KeyEvent.VK_UP) {
			Grid.setDirectionIndex(0);
		} else if (key.getKeyCode() == KeyEvent.VK_RIGHT) {
			Grid.setDirectionIndex(1);
		} else if (key.getKeyCode() == KeyEvent.VK_DOWN) {
			Grid.setDirectionIndex(2);
		} else if (key.getKeyCode() == KeyEvent.VK_LEFT) {
			Grid.setDirectionIndex(3);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	
} 