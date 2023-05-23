package genome;

/*
 * These are the Genes for the Nodes themselves
 */
public class NodeGene extends Gene{
	
	//We need these "coordinates" since we don't have a rigid layer structure like in Convolutional Neural Networks
	//You can only have a connection between a node with a smaller x to one with a larger x
	private double x, y;
	
	public NodeGene(int innovationNumber) {
		super(innovationNumber);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public boolean equals(Object object) {
		if(!(object instanceof NodeGene)) return false;
		return innovationNumber == ((NodeGene) object).getInnovationNumber();
	}
	
	public int hashCode() {
		return innovationNumber;
	}
	

}
