package snake;

public class Coords {
	//Not x,y, coords but 2D array indices
	int row; 
	int col; 
	public Coords(int row, int col) {
		this.row = row;
		this.col = col;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	
	
}
