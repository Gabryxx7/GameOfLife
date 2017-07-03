package it.gabrielemarini.spm.gameoflife.java8;

import it.gabrielemarini.spm.gameoflife.Board;

/**
 * @author Gabryxx7
 *
 */
public class J8Interval {
	private int startRow;
	private int endRow;
	private Board board;
	
	public J8Interval(int pStartRow, int pEndRow, Board board){
		this.startRow = pStartRow;
		this.endRow = pEndRow;
		this.board = board;
	}
	
	public int getStartRow(){
		return this.startRow;
	}
	
	public int getEndRow(){
		return this.endRow;
	}
	
	public Board getBoard(){
		return this.board;
	}
}
