package neat;

import java.util.*;

import genome.ConnectionGene;
import genome.Genome;
import genome.NodeGene;

public class Neat {
	
	public static final int MAX_NODES = (int) Math.pow(2, 20);
	
	private double C1 = 1;
	private double C2 = 1;
	private double C3 = 1;
	
	private double WEIGHT_SHIFT_STRENGTH = 0.3;
	private double WEIGHT_RANDOM_STRENGTH = 1;
	
	
	private double PROBABILITY_MUTATE_CONNECTION = 0.4;
	private double PROBABILITY_MUTATE_NODE = 0.4;
	private double PROBABILITY_MUTATE_WEIGHT_SHIFT = 0.4;
	private double PROBABILITY_MUTATE_WEIGHT_RANDOM = 0.4;
	private double PROBABILITY_MUTATE_TOGGLE_CONNECTION = 0.4;
	
	//The same connection matches to itself, this is simply to leverage a map's ease of use.	
	private HashMap<ConnectionGene, ConnectionGene> allConnections = new HashMap<>(); 
	private ArrayList<NodeGene> allNodes = new ArrayList<>();
	
	private int inputSize;
	private int outputSize;
	private int maxClients;
	
	//The input and output sizes are fixed so we have to set them up here
	public Neat(int inputSize, int outputSize, int clients) {
		this.reset(inputSize, outputSize, clients);
	}
	
	//Create an empty genome
	public Genome emptyGenome() {
		Genome newGenome = new Genome(this);
		//The input and output nodes are mandatory, so we add them.
		//Since they'll all end up in a single hashset, we can do them all at once
		for(int i = 0; i < inputSize + outputSize; i++) {
			newGenome.getNodes().add(getNode(i+1));
		}
		return newGenome;
	}
	
	public ConnectionGene copyConnection(ConnectionGene connection) {
		ConnectionGene connectionGene = new ConnectionGene(connection.getFrom(), connection.getTo());
		connectionGene.setWeight(connection.getWeight());
		connectionGene.setEnabled(connection.isEnabled());
		return connectionGene;
	}
	
	
	//Create a connection between two nodes
	public ConnectionGene getConnection(NodeGene node1, NodeGene node2) {
		ConnectionGene connectionGene = new ConnectionGene(node1, node2);
		
		if(allConnections.containsKey(connectionGene)) {
			//if this connection already exists, inherit its innovation number
			connectionGene.setInnovationNumber(allConnections.get(connectionGene).getInnovationNumber()); 
		}
		else {
			//otherwise give it its own unique innovation number
			connectionGene.setInnovationNumber(allConnections.size()+1);
			allConnections.put(connectionGene,connectionGene);
		}
		return connectionGene;
	}

	
	public void reset(int inputSize, int outputSize, int clients) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.maxClients = clients;
		
		allConnections.clear();
		allNodes.clear();
		
		//inputs and outputs exist regardless of genome, so we create them here
		for(int i = 0; i < inputSize; i++) {
			NodeGene newNode = getNode();
			newNode.setX(0.1); //input nodes all get a x value of 0.1 so they're always to the left of other nodes
			newNode.setY((i+1)/(double)(inputSize+1)); // The input nodes y-value increases so there are no repeats.
		}
		
		for(int i = 0; i < outputSize; i++) {
			NodeGene newNode = getNode();
			newNode.setX(0.9); //output nodes all get a x value of 0.9 so they're always to the right of other nodes
			newNode.setY((i+1)/(double)(outputSize+1)); // The input nodes y-value increases so there are no repeats.
		}
		
	}
	
	
	//Creates a new Node
	public NodeGene getNode() {
		NodeGene newNode = new NodeGene(allNodes.size()+1); //provide the node with its own innovation number
		allNodes.add(newNode);
		return newNode;
	}
	
	//Gets an existing node if it exists
	public NodeGene getNode(int id) {
		if( id <= allNodes.size()) return allNodes.get(id - 1); //-1 since first id would be 1.
		return getNode(); //if the id is not used, return a new node
	}
	
	
	public double getC1() {
		return C1;
	}

	public double getC2() {
		return C2;
	}

	public double getC3() {
		return C3;
	}
	
	public double getWEIGHT_SHIFT_STRENGTH() {
		return WEIGHT_SHIFT_STRENGTH;
	}

	public double getWEIGHT_RANDOM_STRENGTH() {
		return WEIGHT_RANDOM_STRENGTH;
	}
	
	public double getPROBABILITY_MUTATE_CONNECTION() {
		return PROBABILITY_MUTATE_CONNECTION;
	}

	public double getPROBABILITY_MUTATE_NODE() {
		return PROBABILITY_MUTATE_NODE;
	}

	public double getPROBABILITY_MUTATE_WEIGHT_SHIFT() {
		return PROBABILITY_MUTATE_WEIGHT_SHIFT;
	}

	public double getPROBABILITY_MUTATE_WEIGHT_RANDOM() {
		return PROBABILITY_MUTATE_WEIGHT_RANDOM;
	}

	public double getPROBABILITY_MUTATE_TOGGLE_CONNECTION() {
		return PROBABILITY_MUTATE_TOGGLE_CONNECTION;
	}

	public static int getMaxNodes() {
		return MAX_NODES;
	}

	public HashMap<ConnectionGene, ConnectionGene> getAllConnections() {
		return allConnections;
	}

	public ArrayList<NodeGene> getAllNodes() {
		return allNodes;
	}

	public int getInputSize() {
		return inputSize;
	}

	public int getOutputSize() {
		return outputSize;
	}

	public int getMaxClients() {
		return maxClients;
	}

	public static void main(String[] args) {
		Neat neat = new Neat(3,3,100);
		
		Genome genome = neat.emptyGenome();
		System.out.println(genome.getNodes().size());
	}
	

}
