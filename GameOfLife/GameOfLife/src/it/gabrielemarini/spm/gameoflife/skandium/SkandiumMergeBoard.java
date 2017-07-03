package it.gabrielemarini.spm.gameoflife.skandium;

import cl.niclabs.skandium.muscles.Merge;
import it.gabrielemarini.spm.gameoflife.Board;

/**
 * @author Gabryxx7
 *
 */
public class SkandiumMergeBoard implements Merge<Board, Board> {
	Board board;
		
    public SkandiumMergeBoard(Board board){
        this.board = board;
    }
    
    
    @Override
    public Board merge(Board[] param) throws Exception {
        board.swapBoards();
        return board;
    }    

}