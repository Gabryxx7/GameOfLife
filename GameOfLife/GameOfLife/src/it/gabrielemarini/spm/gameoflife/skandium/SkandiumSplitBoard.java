package it.gabrielemarini.spm.gameoflife.skandium;

import cl.niclabs.skandium.muscles.Split;
import it.gabrielemarini.spm.gameoflife.Board;
import it.gabrielemarini.spm.gameoflife.BoardChunk;

/**
 * @author Gabryxx7
 *
 */
public class SkandiumSplitBoard implements Split<Board, SkandiumInterval> {
	private int nParts;
	
	public SkandiumSplitBoard(int pParts){
		this.nParts = pParts;
	}

	@Override
	public SkandiumInterval[] split(Board board) throws Exception {
		SkandiumInterval[] intervals = new SkandiumInterval[nParts];
		BoardChunk[] chunks = board.splitBoard(nParts);
		for (int i = 0; i < nParts; i++) {
			intervals[i] = new SkandiumInterval(chunks[i].getStartRow(), chunks[i].getEndRow()); 
        }
        return intervals;		
	}	
}
