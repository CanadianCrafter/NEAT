package calculations;

import java.util.ArrayList;

public class Node implements Comparable<Node> {
	
	private double x;
	private double output;
	private ArrayList<Connection> connections = new ArrayList<>();
	
	public Node(double x) {
		this.x = x;
		
	}
	
	public void calculate() {
		double sum =0;
		System.out.print(x+": " );
		for(Connection connection: connections) {
			if(connection.isEnabled()) {
				sum += connection.getWeight() * connection.getFrom().getOutput();
				System.out.print(connection.getWeight());
			}
		}
		System.out.println();
		output = activationFunction(sum);
	}
	
	private double activationFunction(double x) {
		//sigmoid activation function
		return 1d/ (1+Math.exp(-x));
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getOutput() {
		return output;
	}

	public void setOutput(double output) {
		this.output = output;
	}

	public ArrayList<Connection> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<Connection> connections) {
		this.connections = connections;
	}

	//TODO: CHECK VALUES IF THEY ARE CORRECT; I think they should be flipped
	@Override
	public int compareTo(Node node2) {
		if(this.x > node2.x) return -1;
		else if(this.x < node2.x) return 1;
		return 0;
	}
	
	
	
	

}
