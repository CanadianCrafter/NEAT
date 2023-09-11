package evolvers;

import neat.*;
import snake.GameGUI;
import snake.Grid;
import visual.Frame;

public class SnakeEvolver {
	
	private static int NUM_GENERATIONS = 400;
	private static int maxScore = 0;
	
	public static void rateIndividual(Grid game, Individual individual, boolean print) {
		game.initialize();
		
		int iteration = 0;
		//to incentive the AI to not dally around; might need to change the threshold to be dynamic for the late game
		int iterationsWithoutApples = 0; 
		int snakeSize = 3;
		double diff = 0;
		while(iteration < 1000 && iterationsWithoutApples < 40 +2*snakeSize && game.getGameState().equals("playing")) {
			iteration++;
			game.aiMove(individual);
			if(print) game.printBoard();
			if(game.getScore() != snakeSize) { //Snake just grew
				iterationsWithoutApples=0;
				snakeSize = (int) game.getScore();
			}
			else {
				iterationsWithoutApples++;
				if(print) System.out.println("Iterations Without Apples: " + iterationsWithoutApples);
			}
			if(iterationsWithoutApples>2) diff+=0.2+1/iterationsWithoutApples;
		}
		individual.setScore(game.getScore()*2+diff);
		if(iterationsWithoutApples== 17 + snakeSize) {
			individual.setScore(game.getScore()*2-2+diff);
			if(print) System.out.println("DEATH BY TIME");
		}
		maxScore = (int) Math.max(maxScore, individual.getScore());
		
		
	}
	
	public static void evolve(Grid game, Neat neat) {
		for(int i =0; i < neat.getMaxIndividuals();i++) {
			rateIndividual(game, neat.getIndividual(i), false);
		}
		neat.evolve();
	}
	
	
	public static void main(String args[]) {
		Grid game = new Grid();
		Neat neat = new Neat(32,4,1000); //modify the input to contain more information
		
		neat.setCP(3);
		neat.setPROBABILITY_MUTATE_CONNECTION(0.3);
		neat.setPROBABILITY_MUTATE_NODE(0.01);
		neat.setPROBABILITY_MUTATE_WEIGHT_RANDOM(0.01);
		neat.setPROBABILITY_MUTATE_WEIGHT_SHIFT(0.01);
		neat.setSURVIVOR_RATE(0.3);
		
		//initialize
		for(int i = 0; i< neat.getMaxIndividuals();i++) {
			rateIndividual(game, neat.getIndividual(i),false);
		}
		neat.evolve();
		for(int i = 0; i < NUM_GENERATIONS; i++) {
			System.out.println("########################" + i + "########################");
			evolve(game, neat);
			neat.printSpecies();
			System.out.println(neat.getBestIndividual().getScore());
			
		}
		
		for(int i = 0; i< neat.getMaxIndividuals();i++) {
			rateIndividual(game, neat.getIndividual(i), false);
		}
		
		System.out.println("BEST INDIVIDUAL" + neat.getBestIndividual().getScore());
		rateIndividual(game, neat.getBestIndividual(),true);
		
		new Frame(neat.getBestIndividual().getGenome());
		

	}

}
