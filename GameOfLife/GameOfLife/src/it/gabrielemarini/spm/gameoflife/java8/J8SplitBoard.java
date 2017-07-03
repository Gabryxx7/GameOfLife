package it.gabrielemarini.spm.gameoflife.java8;

import it.gabrielemarini.spm.gameoflife.Board;
import it.gabrielemarini.spm.gameoflife.BoardChunk;

/**
 * @author Gabryxx7
 *
 */
public class J8SplitBoard{
	private int nParts;
	
	public J8SplitBoard(int pParts){
		this.nParts = pParts;
	}

	public J8Interval[] split(Board board){
		J8Interval[] intervals = new J8Interval[nParts];
		BoardChunk[] chunks = board.splitBoard(nParts);
		for (int i = 0; i < nParts; i++) {
			intervals[i] = new J8Interval(chunks[i].getStartRow(), chunks[i].getEndRow(), board); 
        }
        return intervals;		
	}		
}
