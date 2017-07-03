/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.gabrielemarini.spm.gameoflife.javathreads;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import it.gabrielemarini.spm.gameoflife.Board;

/**
 * @author Gabryxx7
 * Class for defining a single worker
 */
public class Worker implements Runnable {

    private Board board;
    private int startRow, endRow, iterations;
    private final CyclicBarrier barrier;

    /**
     * @param pBoard		The main game board
     * @param pStartRow		The row from which the worker will start computing
     * @param pRows			The amount of rows to iterate
     * @param nIterations	The number of steps to execute on every rows range
     * @param pBarrier		The barrier in which the worker will await fo the other workers
     */
    public Worker(Board pBoard, int pStartRow, int pEndRow, int nIterations, CyclicBarrier pBarrier) {
        board = pBoard;
        startRow = pStartRow;
        endRow = pEndRow;
        iterations = nIterations;
        barrier = pBarrier;
    }


    @Override
    public void run() {  
        for (int i = 0; i < iterations; i++) {
            board.gameStep(startRow, endRow);
            try {
				barrier.await();	//the thread waits in the barrier for the other threads to arrive
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
        }
        return;
    }
}
