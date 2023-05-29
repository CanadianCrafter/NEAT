package neat;

import java.util.Collections;
import java.util.Comparator;

import data_structures.RandomHashSet;
import genome.Genome;

public class Species {
	
	private RandomHashSet<Individual> individuals = new RandomHashSet<>();
	private Individual representative;
	private double score; //The average of the scores of the individuals' score
	
	public Species(Individual representative) { //The representative is the first member of the species
		this.representative = representative;
		this.representative.setSpecies(this);
		individuals.add(representative);
	}
	
	
	public boolean put(Individual individual) {
		if(individual.distance(representative) < representative.getGenome().getNeat().getCP()) {
			individual.setSpecies(this);
			individuals.add(individual);
			return true;
		}
		
		return false;
	}
	
	public void forcePut(Individual individual) {
		individual.setSpecies(this);
		individuals.add(individual);
	}
	
	public void goExtinct() {
		for(Individual individual : individuals.getData()) {
			individual.setSpecies(null);
		}
	}
	
	public void evaluateScore() {
		double totalScore = 0;
		for(Individual individual: individuals.getData()) {
			totalScore += individual.getScore();
		}
		score = totalScore / individuals.size();
	}
	
	public void reset() {
		representative = individuals.randomElement();
		goExtinct();
		individuals.clear();
		
		individuals.add(representative);
		representative.setSpecies(this);
		score = 0;
	}
	
	public void kill(double percentage) {
		individuals.getData().sort(Collections.reverseOrder(
				new Comparator<Individual>() {
					@Override
					public int compare(Individual individual1, Individual individual2) {
						return Double.compare(individual1.getScore(), individual2.getScore());
					}
				}));
		//individuals is now sorted in decreasing order of score
		//We remove the last percentage amount
		//We remove from the back since it's faster than removing from the front (which requires everything to be shifted forwards)		
		for(int i = 0; i< percentage * individuals.size();i++) {
			individuals.get(individuals.size()-1).setSpecies(null); //We always remove from the last index since the size is decreasing
			individuals.remove(individuals.size()-1);
		}
		
	}
	
	public Genome breed() {
		Individual individual1 = individuals.randomElement();
		Individual individual2 = individuals.randomElement();
		
		if(individual1.getScore() > individual2.getScore()) return Genome.crossOver(individual1.getGenome(), individual2.getGenome());
		else return Genome.crossOver(individual2.getGenome(), individual1.getGenome());
	}
	
	public int size() {
		return individuals.size();
	}
	

}
