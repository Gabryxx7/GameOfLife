package it.gabrielemarini.spm.gameoflife.sequential;

import it.gabrielemarini.spm.gameoflife.Board;

/**
 * @author Gabryxx7
 * Sequential version of the game
 */
public class GoLSequential {

	/**
	 * This class simply execute a single step of the game for a fixed amount of times
	 * 
	 * @param board		The main game board
	 * @param iterations	The amount of iterations to execute
	 * @throws InterruptedException
	 */
	public static long execute(Board board, int iterations) throws InterruptedException{
		long startTime = System.currentTimeMillis();
		board.autoGameSteps(iterations);
		long endTime = System.currentTimeMillis();
		
		return (endTime - startTime);
	}
}
