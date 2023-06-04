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
	private static boolean isAlive;
	private static boolean hasWon;
	
	
	public Grid() {
		initialize();
		
	}
	
	private void initialize() {
		gameBoard = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
		
		direction = 'r';
		snake.addLast(new Coords(4,3));
		snake.addLast(new Coords(3,3));
		snake.addLast(new Coords(2,3));
		
		for(int i = 0; i<snake.size();i++) {
			Coords coords = snake.get(i);
			gameBoard[coords.row][coords.col]=true;
		}
		
		directionVectors.put('l', new Integer[] {0,1});
		directionVectors.put('r', new Integer[] {0,-1});
		directionVectors.put('u', new Integer[] {-1,0});
		directionVectors.put('d', new Integer[] {1,0});
		
		generateApple();
		
		score = 0;
		isAlive=true;
		hasWon=false;
		
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
		hasWon=true;
		
		
	}

	public static void updateGrid() {
		
		int newRow = snake.getFirst().row+directionVectors.get(direction)[0];
		int newCol = snake.getFirst().col+directionVectors.get(direction)[1];
		
		if(newRow >= BOARD_HEIGHT || newRow<0 ||newCol < 0|| newCol >= BOARD_WIDTH) {
			//DEAD
			isAlive=false;
		}
		
		snake.addFirst(new Coords(newRow,newCol));
		
		//if the snake eats an apple, the tail doesn't shrink
		if(newRow==apple.row && newCol == apple.col) {
			generateApple();
			score++;
		}
		else {
			snake.removeLast();
		}
				
	}
	
	
	public static boolean[][] getGameBoard() {
		return gameBoard;
	}
	public static void setGameBoard(boolean[][] gameBoard) {
		Grid.gameBoard = gameBoard;
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
	public static void setScore(int score) {
		Grid.score = score;
	}

	@Override
	public String toString() {
		return "Grid [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
	
	static class Coords {
		//Not x,y, coords but 2D array indices
		int row; 
		int col; 
		public Coords(int row, int col) {
			this.row = row;
			this.col = col;
		}

	}
	
	
	
	
	
	
}

