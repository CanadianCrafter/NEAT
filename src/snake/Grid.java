package snake;

import java.util.Arrays;
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
	
	private static double score;
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
		if(openSpaces==0) {
			gameState = "win";
		}
	}

	public static void updateGrid() {
//		printBoard();		
		
		int newRow = snake.getFirst().row+directionVectors.get(directions[directionIndex])[0];
		int newCol = snake.getFirst().col+directionVectors.get(directions[directionIndex])[1];
		
		int tailRow = snake.getLast().row;
		int tailCol = snake.getLast().col;
		
		if(newRow >= BOARD_HEIGHT || newRow<0 ||newCol < 0|| newCol >= BOARD_WIDTH || gameBoard[newRow][newCol]) {
			//DEAD
			if(newRow >= BOARD_HEIGHT || newRow<0 ||newCol < 0|| newCol >= BOARD_WIDTH) {
//				System.out.println("DEATH BY WALL");
//				score-=2;
			}
			else {
//				System.out.println("DEATH BY SUICIDE");
//				score/=3;
			}
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
	
	public void aiMove(Individual individual, int inputSize) {
		double output[] = individual.calculate(aiInput(inputSize));
		
		//get the maximum from the output (the chosen action)
		//index 0 is turn left, index 1 is go straight, index 2 is turn right
		//(Having only three outputs (relative direction) compared to north east south west's four
		//might increase effectiveness??? will test later
		//absolute directions (north, east, south, west)
		int newDirection = 0;
		for(int i = 1; i< output.length; i++) {
			if(output[i]>output[newDirection]) {
				newDirection=i;
			}
		}
		//Used when direction is left/right/forward relative to head
//		int directionChange = index-1;
//		directionIndex = ((((directionIndex+directionChange)%4)+4)%4);
		if(((directionIndex-newDirection+4) %4)!=2) directionIndex = newDirection;
		
		updateGrid();
		
		
	}
	
	public static double[] aiInput(int inputSize) {
		
		double input[] = new double[inputSize];
		
		if(inputSize==24) {
			//For N, E, S, W, NE, SE, SW, NW, we calculate the distance to wall or part of the snake, and the distance of an apple if it is there
			//Then we have the one-hot encoding of the head's direction, and after that the one-hot encoding of the tail's direction
			int headRow = snake.getFirst().getRow();
			int headCol = snake.getFirst().getCol();
			
			//Calculate North Direction
			input[0] = headRow;
			input[1] = (headCol == apple.getCol() && apple.getRow() <= headRow) ? Math.abs(apple.getRow() - headRow) : -1;
			for(int i = headRow -1; i >= 0; i--) {
				if(gameBoard[i][headCol]) {
					input[0]=Math.min(Math.abs(i-headRow),input[0]);
					break;
				}
			}
			//Calculate North East Direction
			input[2] = Math.min(headRow, BOARD_WIDTH-headCol-1)*Math.sqrt(2);
			boolean foundApple = false;
			for(int row = headRow, col = headCol; row>=0 && col < BOARD_WIDTH; row--, col++) {
				if(apple.getRow()==row && apple.getCol()==col) {
					input[3]=Math.abs(row-headRow)*Math.sqrt(2);
					break;
				}
			}
			if(!foundApple) {
				input[3]=-1;
			}
			for(int row = headRow - 1, col = headCol+1; row>=0 && col < BOARD_WIDTH; row--, col++) {
				if(gameBoard[row][col]) {
					input[2]=Math.min(Math.abs(row-headRow), input[2]);
					break;
				}
			}
			
			//Calculate East Direction
			input[4] = BOARD_WIDTH - headCol-1;
			input[5] = (headRow == apple.getRow() && apple.getCol() >= headCol) ? Math.abs(apple.getCol() - headCol) : -1;
			for(int i = headCol + 1; i < BOARD_WIDTH; i++) {
				if(gameBoard[headRow][i]) {
					input[4]=Math.min(Math.abs(i-headCol),input[4]);
					break;
				}
			}
			
			//Calculate South East Direction
			input[6] = Math.min(BOARD_HEIGHT - headRow -1, BOARD_WIDTH-headCol-1)*Math.sqrt(2);
			foundApple = false;
			for(int row = headRow, col = headCol; row< BOARD_HEIGHT && col < BOARD_WIDTH; row++, col++) {
				if(apple.getRow()==row && apple.getCol()==col) {
					input[7]=Math.abs(row-headRow)*Math.sqrt(2);
					break;
				}
			}
			if(!foundApple) {
				input[7]=-1;
			}
			for(int row = headRow + 1, col = headCol+1; row< BOARD_HEIGHT && col < BOARD_WIDTH; row++, col++) {
				if(gameBoard[row][col]) {
					input[6]=Math.min(Math.abs(row-headRow), input[6]);
					break;
				}
			}
			
			//Calculate South Direction
			input[8] = BOARD_HEIGHT - headRow -1;
			input[9] = (headCol == apple.getCol() && apple.getRow() >= headRow) ? Math.abs(apple.getRow() - headRow) : -1;
			for(int i = headRow +1; i < BOARD_HEIGHT; i++) {
				if(gameBoard[i][headCol]) {
					input[8]=Math.min(Math.abs(i-headRow),8);
					break;
				}
			}
			
			//Calculate South West Direction
			input[10] = Math.min(BOARD_HEIGHT - headRow -1, headCol)*Math.sqrt(2);
			foundApple = false;
			for(int row = headRow, col = headCol; row< BOARD_HEIGHT && col>=0; row++, col--) {
				if(apple.getRow()==row && apple.getCol()==col) {
					input[11]=Math.abs(row-headRow)*Math.sqrt(2);
					break;
				}
			}
			if(!foundApple) {
				input[11]=-1;
			}
			for(int row = headRow+1, col = headCol-1; row< BOARD_HEIGHT && col>=0; row++, col--) {
				if(gameBoard[row][col]) {
					input[10]=Math.min(Math.abs(row-headRow), input[10]);
					break;
				}
			}
			
			//Calculate West Direction
			input[12] = headCol;
			input[13] = (headRow == apple.getRow() && apple.getCol() <= headCol) ? Math.abs(apple.getCol() - headCol) : -1;
			for(int i = headCol - 1; i >=0; i--) {
				if(gameBoard[headRow][i]) {
					input[12]=Math.min(Math.abs(i-headCol), input[12]);
					break;
				}
			}
			
			//Calculate North West Direction
			input[14] = Math.min(headRow, headCol)*Math.sqrt(2);
			foundApple = false;
			for(int row = headRow, col = headCol; row>=0 && col >=0 ; row--, col--) {
				if(apple.getRow()==row && apple.getCol()==col) {
					input[15]=Math.abs(row-headRow)*Math.sqrt(2);
					break;
				}
			}
			if(!foundApple) {
				input[15]=-1;
			}
			for(int row = headRow - 1, col = headCol-1; row>=0 && col >=0; row--, col--) {
				if(gameBoard[row][col]) {
					input[14]=Math.min(Math.abs(row-headRow),input[14]);
					break;
				}
			}
			
			//Directions are one hot encoded
			//direction of head
			input[16+directionIndex]=1;
			//direction of tail
			if(snake.getLast().getCol() == snake.get(snake.size()-2).getCol()) {
				//y-axis change
				if(snake.getLast().getRow()>snake.get(snake.size()-2).getRow()) {
					//up
					input[20]=1;
				}
				else {
					//down
					input[21]=1;
				}
				
			}else {
				//x-axis change
				if(snake.getLast().getCol()<snake.get(snake.size()-2).getRow()) {
					//right
					input[22]=1;
				}
				else {
					//left
					input[23]=1;
				}
			}
		}
		
		if(inputSize==32) {
			int index = 2;
			for(int i = 0; i < 8; i ++) {
				input[index+i*3] = 1000;
			}
			//For N, E, S, W, NE, SE, SW, NW, we calculate the distance to wall, if there is an apple, and if there a part of a snake
			//Then we have the one-hot encoding of the head's direction, and after that the one-hot encoding of the tail's direction
			
			int headRow = snake.getFirst().getRow();
			int headCol = snake.getFirst().getCol();
			
			//Calculate North Direction
			input[0] = headRow;
			input[1] = (headCol == apple.getCol() && apple.getRow() <= headRow) ? Math.abs(apple.getRow() - headRow) : -1;
			for(int i = headRow -1; i >= 0; i--) {
				if(gameBoard[i][headCol]) {
					input[2]=Math.abs(i-headRow);
					break;
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
					break;
				}
			}
			
			//Calculate East Direction
			input[6] = BOARD_WIDTH - headCol-1;
			input[7] = (headRow == apple.getRow() && apple.getCol() >= headCol) ? Math.abs(apple.getCol() - headCol) : -1;
			for(int i = headCol + 1; i < BOARD_WIDTH; i++) {
				if(gameBoard[headRow][i]) {
					input[8]=Math.abs(i-headCol);
					break;
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
					break;
				}
			}
			
			//Calculate South Direction
			input[12] = BOARD_HEIGHT - headRow -1;
			input[13] = (headCol == apple.getCol() && apple.getRow() >= headRow) ? Math.abs(apple.getRow() - headRow) : -1;
			for(int i = headRow +1; i < BOARD_HEIGHT; i++) {
				if(gameBoard[i][headCol]) {
					input[14]=Math.abs(i-headRow);
					break;
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
					break;
				}
			}
			
			//Calculate West Direction
			input[18] = headCol;
			input[19] = (headRow == apple.getRow() && apple.getCol() <= headCol) ? Math.abs(apple.getCol() - headCol) : -1;
			for(int i = headCol - 1; i >=0; i--) {
				if(gameBoard[headRow][i]) {
					input[20]=Math.abs(i-headCol);
					break;
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
					break;
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
		}

		
		return input;
	}
	
	public static void printBoard() {
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
		
//		System.out.println(Arrays.toString(aiInput(24)));
		
		
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
//		System.out.println("DIRECTION: " + (((directionIndex+2)%4)+4)%4);
		if((((directionIndex+2)%4)+4)%4!=Grid.directionIndex){
			Grid.directionIndex=directionIndex;
		}
		
	}
	public static double getScore() {
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

