package it.gabrielemarini.spm.gameoflife;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.imageio.ImageIO;


/**
 * @author Gabryxx7
 * This class defines the main board of the game. It uses two byte matrix, one just for reading the cells
 * and another one for writing the new state of the cells
 */
public class Board {

	public final byte ALIVE = 1;
	public final byte DEAD = 0;

	private byte[][] readingBoard, writingBoard;
	private int height, width;

	/**
	 * Starts the iteration counter (used for debug purposes)
	 */
	public Board() {}

	/**
	 * @param height
	 * @param width
	 */
	public Board(int height, int width) {
		this.height = height;
		this.width = width;
		readingBoard = new byte[height][width];
		writingBoard = new byte[height][width];
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	/**
	 * Get the value of a single cell
	 * @param x
	 * @param y
	 * @return
	 */
	public byte getCell(int x, int y) {
		return readingBoard[x][y];
	}

	/**
	 * Set the value of a single cell
	 * @param value
	 * @param x
	 * @param y
	 */
	public void setCell(byte value, int x, int y) {
		readingBoard[x][y] = value;
	}

	/**
	 * Initialize a new board with random values, given its size
	 * @param pHeight
	 * @param pWidth
	 */
	public void initializeRandomBoard(int pHeight, int pWidth) {
		height = pHeight;
		width = pWidth;
		readingBoard = new byte[pHeight][pWidth];
		writingBoard = new byte[pHeight][pWidth];

		Random r = new Random();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				byte number = (byte) (Math.abs(r.nextInt()) % 2);
				readingBoard[i][j] = number;
			}
		}
	}


	/**
	 * Initialize an empty board with all 0s given its size
	 * @param pHeight
	 * @param pWidth
	 */
	public void initializeEmptyBoard(int pHeight, int pWidth) {
		height = pHeight;
		width = pWidth;
		readingBoard = new byte[pHeight][pWidth];
		writingBoard = new byte[pHeight][pWidth];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				readingBoard[i][j] = 0;
			}
		}
	}

	/**
	 * Initialize the board from a source text file
	 * 
	 * @param fileName
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void initializeBoardFromFile(String fileName) throws NumberFormatException, IOException{
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String str;
		height = 0;
		width = 0;

		if((str = in.readLine()) != null)
			height = Integer.parseInt(str);

		if((str = in.readLine()) != null)
			width = Integer.parseInt(str);

		readingBoard = new byte[height][width];
		writingBoard = new byte[height][width];

		for(int i = 0; i < height; i++){
			str = in.readLine();
			for(int j = 0; j < width; j++){
				readingBoard[i][j] =  (byte) (str.charAt(j)-48);	    		
			}
		}
		in.close();
	}


	/**
	 * @param board
	 * @return
	 */
	public boolean copyFromBoard(Board board){
		if(board.getHeight() != height || board.getWidth() != width)
			return false;

		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				readingBoard[i][j] = board.getCell(i, j);
			}
		}    	
		return true;
	}


	/**
	 * Split the board in even parts, computing the startingRow and the chunk size for
	 * each part
	 * 
	 * @param nParts 	The number of parts to split the board in
	 * @return	An array of BoardChunk to store the startingRow and the number of rows to compute for each chunk
	 */
	public BoardChunk[] splitBoard(int nParts){
		BoardChunk[] intervals = new BoardChunk[nParts];

		int startRow = 0;
		int nRows = 0;
		final int step = height / nParts; //Size of the range of rows for every worker
		int remainingRows = height % nParts;

		for (int i = 0; i < nParts; i++) {
			startRow = startRow + nRows;	//The new startingRow is given by adding the number of rows to compute at the previous step
			nRows = step;	//The amount of rows to compute is reset to the step size

			if(remainingRows > 0){	
				nRows++;	//If there is still some extra rows left, we'll add one to the number of rows to compute
				remainingRows--;	//And remove one of the extra rows left
			}			
			intervals[i] = new BoardChunk(startRow, startRow+nRows); 
		}		

		return intervals;
	}


	/**
	 * Print the board to the console where "#" is an alive cell
	 */
	public void printBoard() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if(readingBoard[i][j] == 0)
					System.out.print(" ");
				else System.out.print("#"); 
			}
			System.out.println(" ");
		}
	}

	/**
	 * Compute the alive neighbors given the coordinates of the cell
	 * @param x
	 * @param y
	 * @return The number of alive neighbors of the cell
	 */
	public int computeAliveNeighbors(int x, int y){
		int cnt = 0;
		int prevCol = x-1;
		int prevRow = y-1;
		int nextCol = x+1;
		int nextRow = y+1;

		if(x == 0)
			prevCol = width-1;		

		if(x == width-1)
			nextCol = 0;	

		if(y == 0)
			prevRow = height-1;

		if(y == height-1)
			nextRow = 0;

		cnt += readingBoard[prevCol][prevRow];
		cnt += readingBoard[x][prevRow];
		cnt += readingBoard[nextCol][prevRow];
		cnt += readingBoard[prevCol][y];
		cnt += readingBoard[nextCol][y];
		cnt += readingBoard[prevCol][nextRow];
		cnt += readingBoard[x][nextRow];
		cnt += readingBoard[nextCol][nextRow];

		return cnt;
	}

	/**
	 * 
	 */
	public void swapBoards() {
		byte[][] t = readingBoard;
		readingBoard = writingBoard;
		writingBoard = t;
	}


	/**
	 * Compute a single iteration of the game on the selected range of rows (from startRow to startRow+rows)
	 * @param startRow	Index of the starting row
	 * @param rows		Number of rows to compute from the starting row
	 */
	public void gameStep(int startRow, int endRow){
		for(int i = startRow; i < endRow; i++){
			for(int j=0; j < width; j++){
				boolean isAlive = (readingBoard[i][j] == 0) ? false : true;
				int neighbours = computeAliveNeighbors(i,j);
				if(isAlive){
					if(neighbours == 1 || neighbours == 0)
						writingBoard[i][j] = 0;
					if(neighbours >= 4)
						writingBoard[i][j] = 0;
					if(neighbours == 3 || neighbours == 2)
						writingBoard[i][j] = 1;	
				}
				else {
					if(neighbours == 3)
						writingBoard[i][j] = 1;
					else	writingBoard[i][j] = readingBoard[i][j];
				}
			}
		}
	}


	/**
	 * Executes a single game step for the given amount of times on the whole board
	 * Used by the sequential implementation of the game
	 * 
	 * @param iterations	Number of iterations
	 */
	public void autoGameSteps(int iterations) {
		for (int i = 0; i < iterations; i++) {
			gameStep(0, height);
			swapBoards();
		}
	}

	/**
	 * Write the actual state of the board to a text file where 0 are dead cells while 1 represents an alive cell
	 * 
	 * @param filename	Name of the file to write
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void writeBoardToTextFile(String filename) throws FileNotFoundException{
		PrintWriter writer = new PrintWriter(filename);
		writer.println(height);
		writer.println(width);

		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				writer.print(readingBoard[i][j]);
			}
			writer.println();
		}
		writer.close();
	}

	/**
	 * Compares every cell of two boards of the same size
	 * 
	 * @param boardToCompare	The board to compare
	 * @return false if each cell has the same value, true if even just one cell is different
	 */
	public boolean compareTo(Board boardToCompare){
		if(boardToCompare.getHeight() != height || boardToCompare.getWidth() != width)
			return false;

		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				if(boardToCompare.getCell(i, j) != readingBoard[i][j])
					return false;
			}
		}    	
		return true;
	}


	/**
	 * Write the actual state of the board to a .jpg image file
	 * @param fileName
	 */
	public void writeBoardToImage(String fileName){
		try {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			int aliveColor = 8355968;
			int deadColor = 16776960;

			for(int i = 0; i < height; i++) {
				for(int j = 0; j < width; j++) {

					if(readingBoard[i][j] == 0){
						image.setRGB(j,i,aliveColor);
					}
					else {
						image.setRGB(j,i,deadColor);    	            	
					}
				}
			}
			File outputFile = new File(fileName);
			ImageIO.write(image, "jpg", outputFile);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
