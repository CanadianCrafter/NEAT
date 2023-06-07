package evolvers;

import neat.*;
import snake.Grid;

public class SnakeEvolver {
	
	private static int NUM_GENERATIONS = 200;
	
	public static void rateIndividual(Grid board, Individual individual) {
		board.initialize();
		
		int iteration = 0;
		//to incentivise the AI to not dally around; might need to change the threshold to be dynamic for the late game
		int iterationsWithoutApples = 0; 
		int snakeSize = 0;
		while(iteration< 1000 && iterationsWithoutApples < 20 && board.getGameState().equals("playing")) {
			iteration++;
			board.aiMove(individual);
			if(board.getScore() != snakeSize) { //Snake just grew
				iterationsWithoutApples++;
				snakeSize = board.getScore();
			}
			else {
				iterationsWithoutApples++;
			}
		}
		individual.setScore(board.getScore());
		
		
	}
	
	public static void evolve(Grid board, Neat neat) {
		for(int i =0; i < neat.getMaxIndividuals();i++) {
			rateIndividual(board, neat.getIndividual(i));
		}
		neat.evolve();
	}
	
	
	public static void main(String args[]) {
		Grid game = new Grid();
		Neat neat = new Neat(4,3,1000); //modify the input to contain more information
		
		neat.setCP(3);
		neat.setPROBABILITY_MUTATE_CONNECTION(0.3);
		neat.setPROBABILITY_MUTATE_NODE(0.01);
		neat.setPROBABILITY_MUTATE_WEIGHT_RANDOM(0.01);
		neat.setPROBABILITY_MUTATE_WEIGHT_SHIFT(0.01);
		neat.setSURVIVOR_RATE(0.3);
		
		//initialize
		for(int i = 0; i< neat.getMaxIndividuals();i++) {
			rateIndividual(game, neat.getIndividual(i));
		}
		
		for(int i = 0; i < NUM_GENERATIONS; i++) {
			System.out.println("########################" + i + "########################");
			evolve(game, neat);
			neat.printSpecies();
			
		}
		
		for(int i = 0; i< neat.getMaxIndividuals();i++) {
			rateIndividual(game, neat.getIndividual(i));
		}
		
	}

}
