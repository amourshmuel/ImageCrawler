/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amour.imagecrawler.plugins;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;
/**
 *
 * @author amour
 */
public class ImageResizePlugin implements ImagePlugin{

    private String imageName;
    private int newImageSize;
    @Override
    public String getPluginName() {
        
        return "Image Resize Plugin";
    }
    
    @Override
    public void setImageName(String imageName) {
        
        this.imageName=imageName;
    }

    @Override
    public BufferedImage run(BufferedImage imBuff) {
     
        BufferedImage returnImage;
        returnImage = Scalr.resize(imBuff,newImageSize);
        return returnImage;
    }
    
    public void setNewImageSize(int newImageSize) {
      this.newImageSize = newImageSize;
   }
}