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
	
	public double distance(Genome genome2) {
		return 0;
	}
	
	public static Genome crossOver(Genome genome1, Genome genome2) {
		return null;
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
	
	
}
