/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.gabrielemarini.spm.gameoflife;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JPanel;

/**
 * @author Gabryxx7
 *
 */
public class GraphicBoard extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Board board;
    private BufferedImage buffImg;
    private final int aliveColor = 8355968; //16777215 = White			0 = Black 			8355968 = Grey
    private final int deadColor = 16776960; //9159423 = Bright Blue		4081230 = Grey		16776960 = Yellow
   
    /**
     * @param b
     */
    public GraphicBoard(Board b) {  
        board = b;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics graphics){   
        super.paintComponent(graphics);  
        buffImg = new BufferedImage(board.getWidth(), board.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {            	             
                int color;
                
                if (board.getCell(i, j) == 0)
                	color = aliveColor; //Grey
                else
                	color = deadColor; //Yellow
                
                buffImg.setRGB(j, i, color);
            }
        }
        
        if(imageUpdate(buffImg, ImageObserver.FRAMEBITS,0, 0, buffImg.getWidth(), buffImg.getHeight()))
        	graphics.drawImage(buffImg, 0, 0, Color.BLACK, null); 
    }
}