package it.gabrielemarini.spm.gameoflife;

/**
 * @author Gabryxx7
 *
 * Class that represents a range of rows to compute
 * 
 */
public class BoardChunk {
	private int startRow;
	private int endRow;
	
	public BoardChunk(int pStartRow, int pEndRow){
		this.startRow = pStartRow;
		this.endRow = pEndRow;
	}
	
	public int getStartRow(){
		return this.startRow;
	}
	
	public int getEndRow(){
		return this.endRow;
	}
}
