package it.gabrielemarini.spm.gameoflife.skandium;

import cl.niclabs.skandium.muscles.Execute;
import it.gabrielemarini.spm.gameoflife.Board;

/**
 * @author Gabryxx7
 *
 */
public class SkandiumExecuteSteps implements Execute<SkandiumInterval, Board> {
    Board board;
    
    public SkandiumExecuteSteps(Board board){
        this.board = board;
    }
    
    @Override
    public Board execute(SkandiumInterval param) throws Exception {
        board.gameStep(param.getStartRow(), param.getEndRow());
        return board;
    }
}
