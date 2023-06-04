package snake;

import java.util.HashMap;
import java.util.LinkedList;

public class Grid {
	private final static int BOARD_WIDTH = 10;
	private final static int BOARD_HEIGHT = 10;
	public static boolean gameBoard[][];
	
	private static char direction; //l,r,u,d for left, right, up, down.
	private final static HashMap<Character,Integer[]> directionVectors = new HashMap<>(); //direction, {row, col}
	
	private static LinkedList<Coords> snake = new LinkedList<Coords>();
	
	private static Coords apple;
	
	private static int score;
	private static String gameState; // win, lose, or playing
	
	
	public Grid() {
		initialize();
		
	}
	
	public static void initialize() {
		gameBoard = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
		snake = new LinkedList<Coords>();
		
		direction = 'r';
		snake.addLast(new Coords(3,3));
		snake.addLast(new Coords(3,2));
		snake.addLast(new Coords(3,1));
		
		for(int i = 0; i<snake.size();i++) {
			Coords coords = snake.get(i);
			gameBoard[coords.row][coords.col]=true;
		}
		
		directionVectors.put('l', new Integer[] {0,-1});
		directionVectors.put('r', new Integer[] {0, 1});
		directionVectors.put('u', new Integer[] {-1,0});
		directionVectors.put('d', new Integer[] {1, 0});
		
		generateApple();
		
		score = 3;
		gameState = "playing";
		
	}

	private static void generateApple() {
		int openSpaces = BOARD_HEIGHT*BOARD_WIDTH-snake.size();
		int appleIndex = (int)(openSpaces * Math.random());
		
		int index = -1; 
		//find the appleIndex'th free square
		for(int row = 0; row<BOARD_HEIGHT;row++) {
			for(int col = 0; col<BOARD_WIDTH; col++) {
				if(!gameBoard[row][col]) {
					index++;
					if(index==appleIndex) {
						apple=new Coords(row, col);
						return;
					}
				}
								
			}
		}
		
		//WIN
		gameState = "win";
		
	}

	public static void updateGrid() {
		printBoard();		
		
		int newRow = snake.getFirst().row+directionVectors.get(direction)[0];
		int newCol = snake.getFirst().col+directionVectors.get(direction)[1];
		
		int tailRow = snake.getLast().row;
		int tailCol = snake.getLast().col;
		
		if(newRow >= BOARD_HEIGHT || newRow<0 ||newCol < 0|| newCol >= BOARD_WIDTH || gameBoard[newRow][newCol]) {
			//DEAD
			gameState = "lose";
			return;
		}
		
		//CHECK IF SNAKE BUMPS INTO ITSELF
		
		snake.addFirst(new Coords(newRow,newCol));
		gameBoard[newRow][newCol]=true;
		
		//if the snake eats an apple, the tail doesn't shrink
		if(newRow==apple.row && newCol == apple.col) {
			generateApple();
			score++;
		}
		else {
			snake.removeLast();
			gameBoard[tailRow][tailCol]=false;
		}
				
	}
	
	private static void printBoard() {
		for(int row = 0; row<BOARD_HEIGHT;row++) {
			for(int col = 0; col<BOARD_WIDTH; col++) {
				if(apple.row==row&&apple.col==col) {
					System.out.print("A");
				}
				else if(snake.getFirst().row==row&&snake.getFirst().col==col) {
					System.out.print("H");
				}
				else {
					System.out.print(gameBoard[row][col]?"O":".");
				}
				
			}
			System.out.println();
		}
		System.out.println();
		
	}

	public static Coords getSnakeHead() {
		return snake.getFirst();
	}
	
	public static Coords getApple() {
		return apple;
	}
	
	public static boolean[][] getGameBoard() {
		return gameBoard;
	}
	public static int getBoardWidth() {
		return BOARD_WIDTH;
	}
	public static int getBoardHeight() {
		return BOARD_HEIGHT;
	}
	public static char getDirection() {
		return direction;
	}
	public static void setDirection(char direction) {
		Grid.direction = direction;
	}
	public static int getScore() {
		return score;
	}
	public static String getGameState() {
		return gameState;
	}

	@Override
	public String toString() {
		return "Grid [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
	
	
	
	
}

