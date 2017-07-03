package it.gabrielemarini.spm.gameoflife.skandium;

/**
 * @author Gabryxx7
 *
 */
public class SkandiumInterval {
	private int startRow;
	private int endRow;
	
	public SkandiumInterval(int pStartRow, int pEndRow){
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
