package neat;

import calculations.Calculator;
import genome.Genome;

public class Individual {
	
	private Calculator calculator;

	private Genome genome;
	private double score;
	private Species species;
	
	public void generateCalculator() {
		calculator = new Calculator(genome);
	}
	
	public double[] calculate(double... input) {
		if(calculator == null) {
			generateCalculator();
		}
		return calculator.calculate(input);
	}
	
	public double distance(Individual individual2) {
		return this.getGenome().distance(individual2.getGenome());
	}
	
	public void mutate() {
		getGenome().mutate();
	}
	
	public Calculator getCalculator() {
		return calculator;
	}

	public Genome getGenome() {
		return genome;
	}

	public void setGenome(Genome genome) {
		this.genome = genome;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}
	
	
	
}
