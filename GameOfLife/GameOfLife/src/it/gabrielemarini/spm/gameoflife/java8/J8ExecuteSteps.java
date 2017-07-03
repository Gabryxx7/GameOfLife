package it.gabrielemarini.spm.gameoflife.java8;

import it.gabrielemarini.spm.gameoflife.Board;

/**
 * @author Gabryxx7
 *
 */
public class J8ExecuteSteps {
    
    public J8ExecuteSteps( ){
    }
    
    public Board execute(J8Interval param) {
        param.getBoard().gameStep(param.getStartRow(), param.getEndRow());
        return param.getBoard();
    }
}
