package genome;

import data_structures.RandomHashSet;
import neat.Neat;
import calculations.Calculator;

import java.util.*;

public class Genome {

	private RandomHashSet<ConnectionGene> connections = new RandomHashSet<>();
	private RandomHashSet<NodeGene> nodes = new RandomHashSet<>();
	
	private Neat neat;
	
	public Genome(Neat neat) {
		this.neat = neat;
	}
	
	/*
	 * Calculate the distance between this genome and a second genome, genome2.
	 * We need this since there are disjoint genes which cause genome1 and genome2's genes to be unmatched
	 * 
	 */
	public double distance(Genome genome2) {
		Genome genome1 = this;
		
		//For calculating excess genes later on, we will ensure that genome1's length >= genome2's length
		
		int highestInnovationGene1 = genome1.getConnections().size() != 0 ? genome1.getConnections().get(genome1.getConnections().size()-1).getInnovationNumber() : 0;
		int highestInnovationGene2 = genome2.getConnections().size() != 0 ? genome2.getConnections().get(genome2.getConnections().size()-1).getInnovationNumber() : 0;
		
		if(highestInnovationGene1 < highestInnovationGene2) {
			Genome tempGenome = genome1;
			genome1 = genome2;
			genome2 = tempGenome;
		}
		
		
		int index1 = 0;
		int index2 = 0;
		
		int numSimilar = 0;
		int numDisjoint = 0;
		int numExcess = 0;
		double totalWeightDiff = 0;
		double weightDiff = 0;
		
		while(index1 < genome1.getConnections().size() && index2 < genome2.getConnections().size()) {
			
			ConnectionGene gene1 = genome1.getConnections().get(index1);
			ConnectionGene gene2 = genome2.getConnections().get(index2);
			
			int innovationNum1 = gene1.getInnovationNumber();
			int innovationNum2 = gene2.getInnovationNumber();
			
			if(innovationNum1 == innovationNum2) {
				//Matching Genes
				numSimilar++;
				totalWeightDiff += Math.abs(gene1.getWeight() - gene2.getWeight());
				index1++;
				index2++;
			}
			else if(innovationNum1 > innovationNum2) {
				//Second genome has a disjoint gene here
				numDisjoint++;
				index2++;
			}
			else {
				//First genome has a disjoint gene here
				numDisjoint++;
				index1++;
			}
			
		}
		
		weightDiff /= Math.max(1, numSimilar);
		numExcess = genome1.getConnections().size() - index1; //Since genome1's length >= genome2's length, the rest are excess genes
		
		double N = Math.max(genome1.getConnections().size(), genome2.getConnections().size());
		
		if(N < 20) {
			N=1;
		}
		
		//The number of excess and disjoint genes between a pair of genomes is a measure of their compatibility distance.
		//The more disjoint two genomes are, the less evolutionary history they share, and thus the less compatible they are.
		
		//We return the compatibility distance of different structures in NEAT as a linear combination of the 
		//number of excess E, and disjoint D genes, as well as the average weight differences of the matching genes, W(bar), including disabled genes,
		return neat.getC1() * numDisjoint / N + neat.getC2() * numExcess / N + neat.getC3() * weightDiff;
	}
	
	/*
	 * Creates a child genome from two parent genomes
	 * For matching genes, 50% chance you inherit from genome1, 50% chance you inherit from genome2
	 * For disjoint and excess genes, always inherit from the fitter genome
	 * genome1 has to hold the fitter parent
	 */
	public static Genome crossOver(Genome genome1, Genome genome2) {
		
		Neat neat = genome1.getNeat();
		Genome childGenome = neat.emptyGenome();
		
		int index1 = 0;
		int index2 = 0;
		
		//TODO: Ensure genome1 is fitter (Done, it is ensured in Species since it knows the score)
		
		while(index1 < genome1.getConnections().size() && index2 < genome2.getConnections().size()) {
			
			ConnectionGene gene1 = genome1.getConnections().get(index1);
			ConnectionGene gene2 = genome2.getConnections().get(index2);
			
			int innovationNum1 = gene1.getInnovationNumber();
			int innovationNum2 = gene2.getInnovationNumber();
						
			if(innovationNum1 == innovationNum2) {
				//Matching Genes
				
				//50% chance we pick a gene from genome1, and 50% chance we get one from genome2
				if(Math.random() > 0.5) {
					childGenome.getConnections().add(neat.copyConnection(gene1));
				}
				else {
					childGenome.getConnections().add(neat.copyConnection(gene2));
				}
				index1++;
				index2++;
			}
			else if(innovationNum1 > innovationNum2) {
				//Second genome has a disjoint gene here; we don't add it since it's the less fit genome
				index2++;
			}
			else {
				//First genome has a disjoint gene here; we add it since it is the fitter genome
				childGenome.getConnections().add(neat.copyConnection(gene1));
				index1++;
			}
			
		}
		
		//We add the excess genes of the fitter genome
		while(index1 < genome1.getConnections().size()) {
			ConnectionGene gene1 = genome1.getConnections().get(index1);
			childGenome.getConnections().add(neat.copyConnection(gene1));
			index1++;
		}
		
		for(ConnectionGene c: childGenome.getConnections().getData()) {
			childGenome.getNodes().add(c.getFrom());
			childGenome.getNodes().add(c.getTo());
		}
		
		return childGenome;
		
	}
	
	public void mutate() {
		if(neat.getPROBABILITY_MUTATE_CONNECTION() > Math.random()) {
			mutateConnection();
		}
		if(neat.getPROBABILITY_MUTATE_NODE() > Math.random()) {
			mutateNode();
		}
		if(neat.getPROBABILITY_MUTATE_WEIGHT_SHIFT() > Math.random()) {
			mutateWeightShift();
		}
		if(neat.getPROBABILITY_MUTATE_WEIGHT_RANDOM() > Math.random()) {
			mutateWeightRandom();
		}
		if(neat.getPROBABILITY_MUTATE_TOGGLE_CONNECTION() > Math.random()) {
			mutateConnectionToggle();
		}
		return;

	}
	
	//Create a new connection between two nodes
	public void mutateConnection() {
		//When we create a new connection, the first node's x position must be smaller than the second node's
		//If both have the same x position, we simply have to pick again
		//Likewise, if the connection already exists, we also have to pick again
		//Since this is based on luck, we attempt to do this 100 times until we give up
		
		for(int i = 0; i < 100; i++) {
			NodeGene node1 = nodes.randomElement();
			NodeGene node2 = nodes.randomElement();
			
			if(node1==null || node2==null) continue;
			
			//Try again if both nodes have the same X value
			if(node1.getX()==node2.getX()) { //This also ensures that node1 and node2 aren't the same node
				continue;
			}
			//We want node1 to hold the from node (lower x value) and node2 to hold the to node (higher x value)
			if(node1.getX()> node2.getX()) {
				NodeGene tempNode = node2;
				node2 = node1;
				node1 = tempNode;
			}
			
			ConnectionGene connection = new ConnectionGene(node1,node2);
			
			//Try again if the connection already exists.
			if(connections.contains(connection)) {
				continue;
			}
			
			//Now that we have a valid new connection, this actually creates the connection inside neat
			connection = neat.getConnection(connection.getFrom(), connection.getTo()); 
			connection.setWeight((Math.random()*2-1)*neat.getWEIGHT_RANDOM_STRENGTH());
			
			connections.addSorted(connection); //add it to connections
			
			return;
		}
	}
	
	//Creating a new node between a connection of two existing nodes
	public void mutateNode() {
		ConnectionGene connection = connections.randomElement();
		if(connection==null)return;
		
		NodeGene from = connection.getFrom();
		NodeGene to = connection.getTo();
		
		int replaceIndex = neat.getReplaceIndex(from,to);
		NodeGene middle;
		if(replaceIndex == 0) { //There is no replace index, so we have to create our own new node for a new innovation number
			middle = neat.getNode();
			middle.setX((from.getX()+to.getX())/2);
			middle.setY((from.getY()+to.getY())/2 + Math.random() * 0.1 - 0.05);
			neat.setReplaceIndex(from, to, middle.getInnovationNumber());
		}
		else {
			middle = neat.getNode(replaceIndex);
		}

		
		ConnectionGene connection1 = neat.getConnection(from, middle);
		ConnectionGene connection2 = neat.getConnection(middle, to);
		
		connection1.setWeight(connection.getWeight()); //from node to middle has the weight of the old connection
		connection1.setEnabled(connection.isEnabled()); //connection1 also inherits the old connection's enabled/disabled state
		connection2.setWeight(1); //middle to to node gets a weight of 1. Doing this was found to mitigate issues with new structural additions
		
		connections.remove(connection); //remove old connection? TODO: double check, might just need to disable it
		connections.add(connection1);
		connections.add(connection2);
		
		nodes.add(middle);
		
		return;
		
	}
	
	//Take a connection and shift its weight by a factor
	public void mutateWeightShift() {
		ConnectionGene connection = connections.randomElement();
		if(connection!=null) {
			connection.setWeight(connection.getWeight() + (Math.random()*2-1) * neat.getWEIGHT_SHIFT_STRENGTH());
		}
	}
	
	//Take a connection and assigns a totally random weight to it
	public void mutateWeightRandom() {
		ConnectionGene connection = connections.randomElement();
		if(connection!=null) {
			connection.setWeight((Math.random()*2-1) * neat.getWEIGHT_RANDOM_STRENGTH());
		}
	}
	
	//Toggles the enable/disable state of a connection
	public void mutateConnectionToggle() {
		ConnectionGene connection = connections.randomElement();
		if(connection!=null) {
			connection.setEnabled(!connection.isEnabled());
		}
	}

	public RandomHashSet<ConnectionGene> getConnections() {
		return connections;
	}

	public RandomHashSet<NodeGene> getNodes() {
		return nodes;
	}

	public Neat getNeat() {
		return neat;
	}

	
}
