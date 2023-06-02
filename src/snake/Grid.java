package snake;

public class Grid {
	private final static int BOARD_WIDTH = 80;
	private final static int BOARD_HEIGHT = 80;
	private final static int MARGIN = 4;
	public static boolean gameBoard[][];
	private static boolean tempBoard[][];
	private static boolean originalBoard[][];
	
	public Grid() {
		gameBoard = new boolean[BOARD_HEIGHT+2*MARGIN][BOARD_WIDTH+2*MARGIN];
		tempBoard = new boolean[BOARD_HEIGHT+2*MARGIN][BOARD_WIDTH+2*MARGIN];
		originalBoard = new boolean[BOARD_HEIGHT+2*MARGIN][BOARD_WIDTH+2*MARGIN];
		
	}
	
	public static void updateGrid() {
		for(int row = 0; row < BOARD_HEIGHT+2*MARGIN; row++) {
			for(int col = 0; col < BOARD_WIDTH+2*MARGIN; col++) {
				if(row==0 || row == BOARD_HEIGHT+2*MARGIN-1 || col==0 || col == BOARD_HEIGHT+2*MARGIN-1) {
					tempBoard[row][col]=false;
				}
				else {
					int aliveNeighbours = 0;
					for(int rowMod = -1; rowMod <= 1; rowMod++) {
						for(int colMod = -1; colMod <=1 ; colMod++) {
							if(rowMod==0 && colMod==0) continue;
							else if(gameBoard[row+rowMod][col+colMod]) aliveNeighbours++;
								
						}
					}
					if(gameBoard[row][col]) {
						if (aliveNeighbours == 2 || aliveNeighbours == 3) {
							tempBoard[row][col]=true;
						}
						else {
							tempBoard[row][col]=false;
						}
					}
					else {
						if (aliveNeighbours == 3) {
							tempBoard[row][col]=true;
						}
						else {
							tempBoard[row][col]=false;
						}
					}
						
				}

			}
		}
		
		for(int row = 0; row < BOARD_HEIGHT+2*MARGIN; row++) {
			for(int col = 0; col < BOARD_WIDTH+2*MARGIN; col++) {
				gameBoard[row][col] = tempBoard[row][col];

			}
		}
		
	}
	
	public static void wipeGrid() {	
		for(int row = 0; row < BOARD_HEIGHT+2*MARGIN; row++) {
			for(int col = 0; col < BOARD_WIDTH+2*MARGIN; col++) {
				gameBoard[row][col] = tempBoard[row][col] = false;

			}
		}
		
	}
	
	public static void saveGrid() {	
		for(int row = 0; row < BOARD_HEIGHT+2*MARGIN; row++) {
			for(int col = 0; col < BOARD_WIDTH+2*MARGIN; col++) {
				originalBoard[row][col] = gameBoard[row][col];

			}
		}
		
	}
	
	public static void resetGrid() {	
		for(int row = 0; row < BOARD_HEIGHT+2*MARGIN; row++) {
			for(int col = 0; col < BOARD_WIDTH+2*MARGIN; col++) {
				gameBoard[row][col] = originalBoard[row][col];

			}
		}
		
	}
	
	
	
	public static boolean[][] getGameBoard() {
		return gameBoard;
	}
	public static void setGameBoard(boolean[][] gameBoard) {
		Grid.gameBoard = gameBoard;
	}
	public static boolean[][] getTempBoard() {
		return tempBoard;
	}
	public static void setTempBoard(boolean[][] tempBoard) {
		Grid.tempBoard = tempBoard;
	}
	public static int getBoardWidth() {
		return BOARD_WIDTH;
	}
	public static int getBoardHeight() {
		return BOARD_HEIGHT;
	}
	@Override
	public String toString() {
		return "Grid [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
	
	
	
	
	
	
}
