package snake;

import java.util.HashMap;
import java.util.LinkedList;

import neat.Individual;

public class Grid {
	private final static int BOARD_WIDTH = 10;
	private final static int BOARD_HEIGHT = 10;
	public static boolean gameBoard[][];
	
	private static int directionIndex; //0 for up, 1 for right, 2 for down, 3 for left
	private static char directions[] = {'u','r','d','l'};
	private final static HashMap<Character,Integer[]> directionVectors = new HashMap<>(); //direction, {row, col}
	
	private static LinkedList<Coords> snake = new LinkedList<Coords>();
	
	private static Coords apple;
	
	private static int score;
	private static String gameState; // win, lose, or playing
	
	
	public Grid() {
		directionVectors.put('l', new Integer[] {0,-1});
		directionVectors.put('r', new Integer[] {0, 1});
		directionVectors.put('u', new Integer[] {-1,0});
		directionVectors.put('d', new Integer[] {1, 0});
		initialize();
		
	}
	
	public static void initialize() {
		gameBoard = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
		snake = new LinkedList<Coords>();
		
		directionIndex = 1;
		snake.addLast(new Coords(3,3));
		snake.addLast(new Coords(3,2));
		snake.addLast(new Coords(3,1));
		
		for(int i = 0; i<snake.size();i++) {
			Coords coords = snake.get(i);
			gameBoard[coords.row][coords.col]=true;
		}

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
		
		int newRow = snake.getFirst().row+directionVectors.get(directions[directionIndex])[0];
		int newCol = snake.getFirst().col+directionVectors.get(directions[directionIndex])[1];
		
		int tailRow = snake.getLast().row;
		int tailCol = snake.getLast().col;
		
		if(newRow >= BOARD_HEIGHT || newRow<0 ||newCol < 0|| newCol >= BOARD_WIDTH || gameBoard[newRow][newCol]) {
			//DEAD
			gameState = "lose";
			return;
		}
	
		
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
	
	public static void aiMove(Individual individual) {
		double output[] = individual.calculate(aiInput());
		
		//get the maximum from the output (the chosen action)
		//index 0 is turn left, index 1 is go straight, index 2 is turn left
		//(Having only three outputs (relative direction) compared to north east south west's four
		//might increase effectiveness??? will test later
		int index = 0;
		for(int i = 1; i< output.length; i++) {
			if(output[i]>output[index]) {
				index=i;
			}
		}
		int directionChange = index-1;
		directionIndex = ((((directionIndex+directionChange)%4)+4)%4);
		
		
	}
	
	public static double[] aiInput() {
		double input[] = new double[32];
		
		//For N, E, S, W, NE, SE, SW, NW, we calculate the distance to wall, if there is an apple, and is there a part of a snake
		
		int headRow = snake.getFirst().getRow();
		int headCol = snake.getFirst().getCol();
		
		//Calculate North Direction
		input[0] = headRow;
		input[1] = (headCol == apple.getCol() && apple.getRow() <= headRow) ? Math.abs(apple.getRow() - headRow) : -1;
		for(int i = headRow -1; i >= 0; i--) {
			if(gameBoard[i][headCol]) {
				input[2]=Math.abs(i-headRow);
			}
		}
		//Calculate North East Direction
		input[3] = Math.min(headRow, BOARD_WIDTH-headCol-1)*Math.sqrt(2);
		boolean foundApple = false;
		for(int row = headRow, col = headCol; row>=0 && col < BOARD_WIDTH; row--, col++) {
			if(apple.getRow()==row && apple.getCol()==col) {
				input[4]=Math.abs(row-headRow)*Math.sqrt(2);
				break;
			}
		}
		if(!foundApple) {
			input[4]=-1;
		}
		for(int row = headRow - 1, col = headCol+1; row>=0 && col < BOARD_WIDTH; row--, col++) {
			if(gameBoard[row][col]) {
				input[5]=Math.abs(row-headRow);
			}
		}
		
		//Calculate East Direction
		input[6] = BOARD_WIDTH - headCol-1;
		input[7] = (headRow == apple.getRow() && apple.getCol() >= headCol) ? Math.abs(apple.getCol() - headCol) : -1;
		for(int i = headCol + 1; i < BOARD_WIDTH; i++) {
			if(gameBoard[headRow][i]) {
				input[8]=Math.abs(i-headCol);
			}
		}
		
		//Calculate South East Direction
		input[9] = Math.min(BOARD_HEIGHT - headRow -1, BOARD_WIDTH-headCol-1)*Math.sqrt(2);
		foundApple = false;
		for(int row = headRow, col = headCol; row< BOARD_HEIGHT && col < BOARD_WIDTH; row++, col++) {
			if(apple.getRow()==row && apple.getCol()==col) {
				input[10]=Math.abs(row-headRow)*Math.sqrt(2);
				break;
			}
		}
		if(!foundApple) {
			input[10]=-1;
		}
		for(int row = headRow + 1, col = headCol+1; row< BOARD_HEIGHT && col < BOARD_WIDTH; row++, col++) {
			if(gameBoard[row][col]) {
				input[11]=Math.abs(row-headRow);
			}
		}
		
		
		//Calculate South Direction
		input[12] = BOARD_HEIGHT - headRow -1;
		input[13] = (headCol == apple.getCol() && apple.getRow() >= headRow) ? Math.abs(apple.getRow() - headRow) : -1;
		for(int i = headRow +1; i < BOARD_HEIGHT; i++) {
			if(gameBoard[i][headCol]) {
				input[14]=Math.abs(i-headRow);
			}
		}
		
		
		//Calculate South West Direction
		input[15] = Math.min(BOARD_HEIGHT - headRow -1, headCol)*Math.sqrt(2);
		foundApple = false;
		for(int row = headRow, col = headCol; row< BOARD_HEIGHT && col>=0; row++, col--) {
			if(apple.getRow()==row && apple.getCol()==col) {
				input[16]=Math.abs(row-headRow)*Math.sqrt(2);
				break;
			}
		}
		if(!foundApple) {
			input[16]=-1;
		}
		for(int row = headRow+1, col = headCol-1; row< BOARD_HEIGHT && col>=0; row++, col--) {
			if(gameBoard[row][col]) {
				input[17]=Math.abs(row-headRow);
			}
		}
		
		
		//Calculate West Direction
		input[18] = headCol;
		input[19] = (headRow == apple.getRow() && apple.getCol() <= headCol) ? Math.abs(apple.getCol() - headCol) : -1;
		for(int i = headCol - 1; i >=0; i--) {
			if(gameBoard[headRow][i]) {
				input[20]=Math.abs(i-headCol);
			}
		}
		
		//Calculate North West Direction
		input[21] = Math.min(headRow, headCol)*Math.sqrt(2);
		foundApple = false;
		for(int row = headRow, col = headCol; row>=0 && col >=0 ; row--, col--) {
			if(apple.getRow()==row && apple.getCol()==col) {
				input[22]=Math.abs(row-headRow)*Math.sqrt(2);
				break;
			}
		}
		if(!foundApple) {
			input[22]=-1;
		}
		for(int row = headRow - 1, col = headCol-1; row>=0 && col >=0; row--, col--) {
			if(gameBoard[row][col]) {
				input[23]=Math.abs(row-headRow);
			}
		}
			
		
		//Directions are one hot encoded
		//direction of head
		input[24+directionIndex]=1;
		//direction of tail
		if(snake.getLast().getCol() == snake.get(snake.size()-2).getCol()) {
			//y-axis change
			if(snake.getLast().getRow()>snake.get(snake.size()-2).getRow()) {
				//up
				input[28]=1;
			}
			else {
				//down
				input[30]=1;
			}
			
		}else {
			//x-axis change
			if(snake.getLast().getCol()<snake.get(snake.size()-2).getRow()) {
				//right
				input[29]=1;
			}
			else {
				//left
				input[31]=1;
			}
		}
		
		return null;
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
	public static int getDirectionIndex() {
		return directionIndex;
	}
	public static void setDirectionIndex(int directionIndex) {
		if((((directionIndex+2)%4)+4)%4!=directionIndex){
			Grid.directionIndex=directionIndex;
		}
		
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

