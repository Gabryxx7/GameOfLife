package it.gabrielemarini.spm.gameoflife.javathreads;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import it.gabrielemarini.spm.gameoflife.Board;
import it.gabrielemarini.spm.gameoflife.BoardChunk;

/**
 * @author Gabryxx7
 * Class for the parallel version of the game implemented with java threads
 */
public class GoLJavaThreads {
	
	/**
	 * @param board		The main game board
	 * @param iterations	Amount of iterations to compute
	 * @param nThreads		Number of threads to use
	 * @throws InterruptedException
	 */
	public static long execute(Board board, int iterations, int nThreads) throws InterruptedException{	
		//Common CyclicBarrier that will swap the two boards pointers
		final CyclicBarrier barrier = new CyclicBarrier(nThreads, board::swapBoards);

		BoardChunk[] chunks = board.splitBoard(nThreads);
		ExecutorService threadPool = Executors.newFixedThreadPool(nThreads); //Creating the threadPool

		long startTime = System.currentTimeMillis();
		//Create and execute a new worker for the selected number of threads
		for (int j = 0; j < nThreads; j++) {			
            threadPool.execute(new Worker(board, chunks[j].getStartRow(), chunks[j].getEndRow(), iterations, barrier));
		}		
		threadPool.shutdown(); //Shutting down the threadPool

		threadPool.awaitTermination(10, TimeUnit.MINUTES);		//If after 10 minutes, the threadPool is not closed, terminates the execution
		long endTime = System.currentTimeMillis();
		
		return (endTime - startTime);
	}
}