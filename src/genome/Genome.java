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
		
		//Since genome1 has to have the larger innovation number, if this genome has a smaller one, we swap the two
		//We do this because we want to copy more information from the fitter genome
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
				//Similar Genes
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
		numExcess = genome1.getConnections().size() - index1;
		
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
	
	public static Genome crossOver(Genome genome1, Genome genome2) {
		
		Neat neat = genome1.getNeat();
		Genome genome = neat.emptyGenome();
		
		int index1 = 0;
		int index2 = 0;
		
		while(index1 < genome1.getConnections().size() && index2 < genome2.getConnections().size()) {
			
			ConnectionGene gene1 = genome1.getConnections().get(index1);
			ConnectionGene gene2 = genome2.getConnections().get(index2);
			
			int innovationNum1 = gene1.getInnovationNumber();
			int innovationNum2 = gene2.getInnovationNumber();
			
			
			if(innovationNum1 == innovationNum2) {
				//Similar Genes
				
				//50% chance we pick a gene from genome1, and 50% change we get one from genome2
				if(Math.random() > 0.5) {
					genome.getConnections().add(neat.copyConnection(gene1));
				}
				else {
					genome.getConnections().add(neat.copyConnection(gene2));
				}
				index1++;
				index2++;
			}
			else if(innovationNum1 > innovationNum2) {
				//Second genome has a disjoint gene here
				index2++;
			}
			else {
				//First genome has a disjoint gene here
				genome.getConnections().add(neat.copyConnection(gene1));
				index1++;
			}
			
		}
		
		while(index1 < genome1.getConnections().size()) {
			ConnectionGene gene1 = genome1.getConnections().get(index1);
			genome.getConnections().add(neat.copyConnection(gene1));
			index1++;
		}
		
		for(ConnectionGene c: genome.getConnections().getData()) {
			genome.getNodes().add(c.getFrom());
			genome.getNodes().add(c.getTo());
		}
		
		return genome;
		
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
