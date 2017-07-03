package it.gabrielemarini.spm.gameoflife.tests;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;

import it.gabrielemarini.spm.gameoflife.Board;
import it.gabrielemarini.spm.gameoflife.GraphicBoard;
import it.gabrielemarini.spm.gameoflife.java8.GoLJava8;
import it.gabrielemarini.spm.gameoflife.javathreads.GoLJavaThreads;
import it.gabrielemarini.spm.gameoflife.sequential.GoLSequential;
import it.gabrielemarini.spm.gameoflife.skandium.GoLSkandium;

public class Utilities {

	/**
	 * Debugging class, creates a random board or gets one from a specified file, compute the result after
	 * a number of iterations and display it graphically in different windows
	 * 
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException, ExecutionException, NumberFormatException, IOException{
//		splitBoardTest();
		Board[] boards = new Board[4];
		Board randomBoard = new Board();
		randomBoard.initializeRandomBoard(500, 500);
		
		for(int i = 0; i < boards.length; i++){
			boards[i] = new Board();
//			boards[i].initializeBoardFromFile("glider.txt");
//			System.out.println(boards[i].getHeight()+" " +boards[i].getWidth());
			boards[i].initializeEmptyBoard(randomBoard.getHeight(), randomBoard.getWidth());
			boards[i].copyFromBoard(randomBoard);
		}		

		createWindow("Game of Life - Sequential", boards[0], 0);	
		GoLSequential.execute(boards[0], 1000);
		
		createWindow("Game of Life - Java Threads", boards[1], 100);	
		GoLJavaThreads.execute(boards[1], 1000, 4);
		
		createWindow("Game of Life - Skandium", boards[2], 200);	
		GoLSkandium.execute(boards[2], 1000, 4);
		
		createWindow("Game of Life - Java8", boards[3], 300);	
		GoLJava8.execute(boards[3], 1000, 4);		
		
		//Wait for user to terminate the execution, useful for checking the resulting boards
		new Scanner(System.in).nextLine(); 
		System.exit(0);
	}
	
	
	public static void createWindow(String Title, Board board, int offset){
		JFrame frame = new JFrame(Title);	
		Graphics g = frame.getGraphics();
		frame.pack();
		Insets insets = frame.getInsets();
		frame.getContentPane().add(new GraphicBoard(board), BorderLayout.CENTER);
		frame.paint(g);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(insets.left + insets.right + board.getWidth(), insets.top + insets.bottom + board.getHeight());
//		frame.setLocation(offset, 0);
		frame.setVisible(true);			
	}
	
	
	public static void splitBoardTest(){
		Board board = new Board();
		board.initializeRandomBoard(1000, 1000);
		for(int t = 1; t <= Runtime.getRuntime().availableProcessors(); t++){
			System.out.println("\nSplitting 1000x1000 board in " +t +" parts");
			board.splitBoard(t);
		}
		System.exit(0);
	}
}
