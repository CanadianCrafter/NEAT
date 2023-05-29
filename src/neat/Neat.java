package neat;

import java.util.*;

import data_structures.RandomHashSet;
import data_structures.RandomSelector;
import genome.ConnectionGene;
import genome.Genome;
import genome.NodeGene;
import visual.Frame;

public class Neat {
	
	public static final int MAX_NODES = (int) Math.pow(2, 20);
	
	private double C1 = 1;
	private double C2 = 1;
	private double C3 = 1;
	
	private double CP = 4;
	
	private double WEIGHT_SHIFT_STRENGTH = 0.3;
	private double WEIGHT_RANDOM_STRENGTH = 1;
	
	private double SURVIVOR_RATE = 0.8;
	
	private double PROBABILITY_MUTATE_CONNECTION = 0.01;
	private double PROBABILITY_MUTATE_NODE = 0.003;
	private double PROBABILITY_MUTATE_WEIGHT_SHIFT = 0.002;
	private double PROBABILITY_MUTATE_WEIGHT_RANDOM = 0.002;
	private double PROBABILITY_MUTATE_TOGGLE_CONNECTION = 0;
	
	//The same connection matches to itself, this is simply to leverage a map's ease of use.	
	private HashMap<ConnectionGene, ConnectionGene> allConnections = new HashMap<>(); 
	private ArrayList<NodeGene> allNodes = new ArrayList<>();
	
	private RandomHashSet<Individual> individuals = new RandomHashSet<>();
	private RandomHashSet<Species> species = new RandomHashSet<>();
	
	private int inputSize;
	private int outputSize;
	private int maxIndividuals;
	
	//The input and output sizes are fixed so we have to set them up here
	public Neat(int inputSize, int outputSize, int individuals) {
		this.reset(inputSize, outputSize, individuals);
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

	
	public void reset(int inputSize, int outputSize, int individuals) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.maxIndividuals = individuals;
		
		allConnections.clear();
		allNodes.clear();
		this.individuals.clear();
		
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
		
		for(int i =0; i < maxIndividuals; i++) {
			Individual individual = new Individual();
			individual.setGenome(emptyGenome());
			individual.generateCalculator();
			this.individuals.add(individual);
			
		}
		
	}
	
	public void evolve() {
		generateSpecies(); //This groups the individuals into the species
		kill(); //Kill some percentage of the individuals
		removeExtinctSpecies(); //If any species goes extinct, remove them
		reproduce(); //The clients that gone extinct get replaced by the offspring of two random individuals from the same species (randomly chosen)
		mutate(); //mutate everything
		for(Individual individual: individuals.getData()) {
			individual.generateCalculator(); //calculate everything
		}
		
	}
	
	private void generateSpecies() {
		//Reset all the species; this doesn't remove the individuals, just resets all the species classifications
		for(Species s: species.getData()) {
			s.reset();
		}
		
		for(Individual i: individuals.getData()) {
			if(i.getSpecies() != null) continue;
			
			//See if the individual fits in one of the existing species
			boolean joinedSpecies = false;
			for(Species s: species.getData()) {
				if(s.put(i)) {
					joinedSpecies = true;
					break;
					
				}
			}
			//If the individual doesn't fit into any of the existing species, it starts its own species
			if(!joinedSpecies) {
				species.add(new Species(i));
			}
		}
		
		//Evaluate the score of all species
		for(Species s: species.getData()) {
			s.evaluateScore();
		}
//		System.out.println(species.size());
	}
	
	
	private void kill() {
		for(Species s: species.getData()) {
			s.kill(1-SURVIVOR_RATE);
		}
	
	}
	
	private void removeExtinctSpecies() {
		//We iterate backwards since species is a RandomHashSet which uses an ArrayList, and deleting is O(1) from the back
		for(int i =species.size()-1; i>=0; i--) { 
			if(species.get(i).size() <= 1) {  //If a species only has one or less individuals, it goes extinct
				species.get(i).goExtinct();
				species.remove(i);
			}
		}
	}
	
	private void reproduce() {
		RandomSelector<Species> selector = new RandomSelector<>();
		for(Species s: species.getData()) {
			selector.add(s, s.getScore());
		}
		
//		System.out.println(selector.size());
		
		for(Individual i: individuals.getData()) {
			if(i.getSpecies()==null) {
				Species s = selector.random();
				i.setGenome(s.breed());
				s.forcePut(i);
			}
		}
	}
	
	private void mutate() {
		for(Individual i: individuals.getData()) {
			i.mutate();
		}
	}
	
	public void printSpecies() {
		System.out.println("################################################################");
		for(Species s: this.species.getData()) {
			System.out.println(s + " " + s.getScore() + " " + s.size());
		}
	}
	
	//Get the individual based on their index
	public Individual getIndividual(int index) {
		return individuals.get(index);
		
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
	
	public double getCP() {
		return CP;
	}

	public void setCP(double cP) {
		CP = cP;
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

	public int getMaxIndividuals() {
		return maxIndividuals;
	}

	public static void main(String[] args) {
		Neat neat = new Neat(10,1,1000);
		double input[] = new double[10];
		
		for(int i = 0; i< 10; i++) input[i] = Math.random();
		
		for(int i = 0; i< 1000; i++) {
			for(Individual ind:neat.individuals.getData()) {
				double score = ind.calculate(input)[0];
				ind.setScore((score));
			}
			neat.evolve();
			neat.printSpecies();
		}
//		new Frame(neat.emptyGenome());
	}
	

}
