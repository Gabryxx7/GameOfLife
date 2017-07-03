package it.gabrielemarini.spm.gameoflife.java8;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import it.gabrielemarini.spm.gameoflife.Board;

/**
 * @author Gabryxx7
 * Class for the Java8 implementation of the game using the java8 streams feature
 */
public class GoLJava8 {
    /**
     * @param board		The main game board
     * @param iterations	Number of iterations to execute
     * @param nThreads		Number of threads to use
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws Exception
     */
    public static long execute(Board board, int iterations, int nThreads) throws InterruptedException, ExecutionException{
    	
    	ForkJoinPool fjPool = new ForkJoinPool(nThreads); //Create a new pool of threads
    	
		/** Submit the lambda to the pool, at every iteration:
		 * 
		 * 	1. The board is split into an array of intervals and
		 *  2. A stream of intervals is generated from the computed array
		 *  3. Each stream is parallelized
		 *  4. Each interval of the parallelized stream is passed to the execute method
		 *  	To execute a single step of the game
		 *  5. After the execution, the boards pointer are swapped calling the merge method
		 */
		long startTime = System.currentTimeMillis();
    	for(int i = 0; i < iterations; i++){  
	    	fjPool.submit(() -> Arrays.stream(new J8SplitBoard(nThreads).split(board))
							.parallel()
							.forEach(interval -> new J8ExecuteSteps().execute(interval))).get();
	    	
	    	J8MergeBoard.merge(board);
    	}	
		long endTime = System.currentTimeMillis();
	        
		fjPool.shutdown();
		
		return (endTime - startTime);
    }

}
