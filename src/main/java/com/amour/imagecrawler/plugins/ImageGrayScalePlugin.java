/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amour.imagecrawler.plugins;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
/**
 *
 * @author amour
 */
public class ImageGrayScalePlugin implements ImagePlugin{

    private String imageName;
    
    @Override
    public String getPluginName() {
        
        return "Image Gray Scale Plugin";
    }
    
    @Override
    public void setImageName(String imageName) {
        
        this.imageName=imageName;
    }

    @Override
    public BufferedImage run(BufferedImage imBuff) {
     
        BufferedImage image = new BufferedImage(imBuff.getWidth(), imBuff.getHeight()
                ,BufferedImage.TYPE_BYTE_GRAY);  
        
        Graphics g = image.getGraphics();
        g.drawImage(imBuff, 0, 0, null);
        g.dispose();  
       
        return image;
    }
}