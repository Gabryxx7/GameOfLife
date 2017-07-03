package it.gabrielemarini.spm.gameoflife.java8;

import it.gabrielemarini.spm.gameoflife.Board;

/**
 * @author Gabryxx7
 *
 */
public class J8MergeBoard{
		
    public J8MergeBoard( ){}
    
    
    public static Board merge(Board param){
    	param.swapBoards();
        return param;
    }  

}