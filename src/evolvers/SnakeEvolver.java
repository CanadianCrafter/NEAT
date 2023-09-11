package evolvers;

import neat.*;
import snake.GameGUI;
import snake.Grid;
import visual.Frame;

public class SnakeEvolver {
	
	private static int NUM_GENERATIONS = 10;
	private static int maxScore = 0;
	
	public static void rateIndividual(GameGUI game, Individual individual) {
		game.grid.initialize();
		
		int iteration = 0;
		//to incentive the AI to not dally around; might need to change the threshold to be dynamic for the late game
		int iterationsWithoutApples = 0; 
		int snakeSize = 3;
		while(iteration < 1000 && iterationsWithoutApples < 30 + snakeSize && game.grid.getGameState().equals("playing")) {
			iteration++;
			game.grid.aiMove(individual);
			game.grid.printBoard();
			if(game.grid.getScore() != snakeSize) { //Snake just grew
				iterationsWithoutApples=0;
				snakeSize = game.grid.getScore();
			}
			else {
				iterationsWithoutApples++;
			}
		}
		individual.setScore(game.grid.getScore());
		maxScore = (int) Math.max(maxScore, individual.getScore());
		
	}
	
	public static void evolve(GameGUI game, Neat neat) {
		for(int i =0; i < neat.getMaxIndividuals();i++) {
			rateIndividual(game, neat.getIndividual(i));
		}
		neat.evolve();
	}
	
	
	public static void main(String args[]) {
		GameGUI game = new GameGUI(1);
		Neat neat = new Neat(32,3,1); //modify the input to contain more information
		
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
		neat.evolve();
		for(int i = 0; i < NUM_GENERATIONS; i++) {
			System.out.println("########################" + i + "########################");
			evolve(game, neat);
			neat.printSpecies();
			System.out.println(neat.getBestIndividual().getScore());
			
		}
		
		for(int i = 0; i< neat.getMaxIndividuals();i++) {
			rateIndividual(game, neat.getIndividual(i));
		}
		
		new Frame(neat.getBestIndividual().getGenome());
		

	}

}
