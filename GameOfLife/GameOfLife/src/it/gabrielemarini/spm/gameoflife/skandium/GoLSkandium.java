package it.gabrielemarini.spm.gameoflife.skandium;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cl.niclabs.skandium.Skandium;
import cl.niclabs.skandium.Stream;
import cl.niclabs.skandium.skeletons.For;
import cl.niclabs.skandium.skeletons.Map;
import cl.niclabs.skandium.skeletons.Skeleton;
import it.gabrielemarini.spm.gameoflife.Board;

/**
 * @author Gabryxx7
 * Skandium implementation of the game
 */
public class GoLSkandium {
	
	/**
	 * This method execute the Skandium skeleton in a For loop using the provided For
	 * 
	 * @param board		The main game board
	 * @param iterations	Number of iterations to execute on the board
	 * @param nThreads 		Number of threads to use
	 * @return skandiumResult 	The resulting computed board
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static long execute(Board board, int iterations, int nThreads) throws InterruptedException, ExecutionException{
		Skandium skandium = new Skandium(nThreads);
		
		Skeleton<Board, Board> GoLMap = new Map<Board, Board>(
				new SkandiumSplitBoard(nThreads),
				new SkandiumExecuteSteps(board),
				new SkandiumMergeBoard(board) );

		/** There was a bug here, the for loop was being executed an infinite number of times **/
		/** FIXED: using the java version found on github, a file diff.txt with the output of the command diff -r has been included in the project **/
		Skeleton<Board, Board> loop = new For<Board>(GoLMap, iterations);	        
		Stream<Board,Board> stream = skandium.newStream(loop);

		Future<Board> future = stream.input(board);	   
		long startTime = System.currentTimeMillis();
		future.get();
		long endTime = System.currentTimeMillis();
		skandium.shutdown();
		
		return (endTime - startTime);
	}
}
