package calculations;

import genome.*;
import data_structures.*;

import java.util.*;

public class Calculator {
	
	private ArrayList<Node> inputNodes = new ArrayList<>();
	private ArrayList<Node> hiddenNodes = new ArrayList<>();
	private ArrayList<Node> outputNodes = new ArrayList<>();
	
	public Calculator(Genome genome) {
		RandomHashSet<NodeGene> nodes = genome.getNodes();
		RandomHashSet<ConnectionGene> connections = genome.getConnections();
		
		
		HashMap<Integer, Node> nodeHashMap = new HashMap<>();
		
		for(NodeGene n: nodes.getData()) {
			Node node = new Node(n.getX());
			nodeHashMap.put(n.getInnovationNumber(), node);
			if(n.getX() <= 0.1) { //This is by definition
				inputNodes.add(node);
			}
			else if(n.getX() >= 0.9) { //This is also by definition
				outputNodes.add(node);
			}
			else {
				hiddenNodes.add(node);
			}
		}
		
		//This seems to sort it in descending order, which doesn't seem right
		hiddenNodes.sort(new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				return n1.compareTo(n2);
			}
		});
		
		for (ConnectionGene c: connections.getData()) {
			NodeGene from = c.getFrom();
			NodeGene to = c.getTo();
			
			//These are the nodes inside the calculator
			Node nodeFrom = nodeHashMap.get(from.getInnovationNumber());
			Node nodeTo = nodeHashMap.get(to.getInnovationNumber());
			
			Connection connection = new Connection(nodeFrom, nodeTo);
			connection.setWeight(c.getWeight());
			connection.setEnabled(c.isEnabled());
			
			nodeTo.getConnections().add(connection);
			
			
		}

		
		
	}
	
	public double[] calculate(double... input) {
		if(input.length != inputNodes.size()) throw new RuntimeException("ERROR: Input data doesn't match.");
		for(int i = 0; i <inputNodes.size();i++) {
			inputNodes.get(i).setOutput(input[i]);
		}
		
		
		// I have a bad feeling about this; since it's sorted in descending order, 
		//it'll first calculate the nodes in the back, then reach the nodes at the front
		for(Node node: hiddenNodes) {
			node.calculate();
		}
		
		double[] output = new double[outputNodes.size()];
		for(int i =0;i< outputNodes.size();i++) {
			outputNodes.get(i).calculate();
			output[i] = outputNodes.get(i).getOutput();
		}
		
		return output;
		
		
		
	}

}
