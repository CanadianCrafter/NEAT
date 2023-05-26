package genome;

import data_structures.RandomHashSet;
import neat.Neat;

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
		int highestInnovationGene1 = genome1.getConnections().get(genome1.getConnections().size()-1).getInnovationNumber();
		int highestInnovationGene2 = genome2.getConnections().get(genome2.getConnections().size()-1).getInnovationNumber();
		
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
		
		weightDiff =totalWeightDiff/ numSimilar;
		numExcess = genome1.getConnections().size() - index1; //Since genome1's length >= genome2's length, the rest are excess genes
		
		double N = Math.max(genome1.getConnections().size(), genome2.getConnections().size());
		
		if(N < 20) {
			N=1;
		}
		
		//The number of excess and disjoint genes between a pair of genomes is a measure of their compatibility distance.
		//The more disjoint two genomes are, the less evolutionary history they share, and thus the less compatible they are.
		
		//We return the compatibility distance of different structures in NEAT as a linear combination of the 
		//number of excess E, and disjoint D genes, as well as the average weight differences of the matching genes, W(bar), including disabled genes,
		return ((numDisjoint / N) + (numExcess / N) + weightDiff);
	}
	
	/*
	 * Creates a child genome from two parent genomes
	 * For matching genes, 50% chance you inherit from genome1, 50% chance you inherit from genome2
	 * For disjoint and excess genes, always inherit from the fitter genome
	 * We make genome1 hold the fitter parent
	 */
	public static Genome crossOver(Genome genome1, Genome genome2) {
		
		Neat neat = genome1.getNeat();
		Genome childGenome = neat.emptyGenome();
		
		int index1 = 0;
		int index2 = 0;
		
		//TODO: Ensure genome1 is fitter
		
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
	
	public void mutuate() {
		return;
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
