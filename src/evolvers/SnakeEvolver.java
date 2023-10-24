package evolvers;

import java.util.Timer;
import java.util.TimerTask;

import neat.*;
import snake.GameGUI;
import snake.Grid;
import visual.Frame;

public class SnakeEvolver {
	
	private static int NUM_GENERATIONS = 400;
	private static int maxScore = 0;
	private static boolean debug = false;
	
	public static void rateIndividual(Grid game, Individual individual, boolean display, int inputSize) {
		if(display) {
//			GameGUI gameGUI = new GameGUI(2);
			game.initialize();
			Timer timer = new Timer();

			timer.schedule( new TimerTask() {
			    public void run() {
			    	if(!game.getGameState().equals("playing")) {
			    		System.out.println("LOSE");
			    		System.out.println(game.getScore());
			    		timer.cancel();
			    	}
			    	game.aiMove(individual, inputSize);
			    	game.printBoard();
			    }
			 }, 0, 1*1000);
		}
		else {
			game.initialize();
			
			int iteration = 0;
			//to incentive the AI to not dally around; might need to change the threshold to be dynamic for the late game
			int iterationsWithoutApples = 0; 
			int snakeSize = 3;
			double diff = 0;
			while(iteration < 1000 && iterationsWithoutApples < 40 +2*snakeSize && game.getGameState().equals("playing")) {
				iteration++;
				game.aiMove(individual, inputSize);
				if(debug&&display) game.printBoard();
				if(game.getScore() != snakeSize) { //Snake just grew
					//reward snake for getting it quickly
//					diff+= Math.max((-1/10)*iterationsWithoutApples+5,0);
					iterationsWithoutApples=0;
					snakeSize = (int) game.getScore();		
					diff+=-Math.pow((iterationsWithoutApples-30),3)/40000-4*Math.tanh((iterationsWithoutApples-30)/20); //one time or continuous?
				}
				else {
					iterationsWithoutApples++;
					if(debug&&display) System.out.println("Iterations Without Apples: " + iterationsWithoutApples);
				}
				//consider using a negative tanh function so that it eventually punishes living too long
//				if(iterationsWithoutApples>2) diff+=(0.2+1/iterationsWithoutApples)*(40-iterationsWithoutApples)/Math.abs(iterationsWithoutApples==40?1:40-iterationsWithoutApples); 
//				diff+=-4*Math.tanh((iterationsWithoutApples-40)/20);
//				diff+=-(iterationsWithoutApples-40)/10;
//				diff+=-Math.pow((iterationsWithoutApples-40),3)/10000;
				diff+=0.3;
				
			}
//			diff+=Math.max(-(iteration)/100+3,0); //reward living long
//			individual.setScore(game.getScore()*4+diff);
			individual.setScore(diff+game.getScore());
			if(iterationsWithoutApples== 40 +2*snakeSize) {
//				individual.setScore(game.getScore()*4-2+diff); 
				individual.setScore(diff-3+game.getScore());
				if(debug&&display) System.out.println("DEATH BY TIME");
			}
			maxScore = (int) Math.max(maxScore, individual.getScore());
		}
		
		
		
		
		
	}
	
	public static void evolve(Grid game, Neat neat) {
		for(int i =0; i < neat.getMaxIndividuals();i++) {
			rateIndividual(game, neat.getIndividual(i), false,neat.getInputSize());
		}
		neat.evolve();
	}
	
	
	public static void main(String args[]) {
		Grid game = new Grid();
		Neat neat = new Neat(24,4,1000); //modify the input to contain more information
		
		neat.setCP(3);
		neat.setPROBABILITY_MUTATE_CONNECTION(0.32);
		neat.setPROBABILITY_MUTATE_NODE(0.01);
		neat.setPROBABILITY_MUTATE_WEIGHT_RANDOM(0.01);
		neat.setPROBABILITY_MUTATE_WEIGHT_SHIFT(0.01);
		neat.setSURVIVOR_RATE(0.5);
		
		//initialize
		for(int i = 0; i< neat.getMaxIndividuals();i++) {
			rateIndividual(game, neat.getIndividual(i),false,neat.getInputSize());
		}
		neat.evolve();
		for(int i = 0; i < NUM_GENERATIONS; i++) {
			System.out.println("########################" + i + "########################");
			evolve(game, neat);
			neat.printSpecies();
			System.out.println(neat.getBestIndividual().getScore());
			
		}
		
		for(int i = 0; i< neat.getMaxIndividuals();i++) {
			rateIndividual(game, neat.getIndividual(i), false, neat.getInputSize());
		}
		
		System.out.println("BEST INDIVIDUAL" + neat.getBestIndividual().getScore());
		rateIndividual(game, neat.getBestIndividual(),true, neat.getInputSize());
		
		new Frame(neat.getBestIndividual().getGenome());
		

	}

}
