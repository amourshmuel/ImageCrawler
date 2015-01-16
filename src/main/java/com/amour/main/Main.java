/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amour.main;

import com.amour.imagecrawler.Crawler;
import java.util.Arrays;
import java.util.logging.Level;

import org.apache.log4j.Logger;

/**
 *
 * @author amour
 */
public class Main {

    final static Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args) {
        
        try {
            logger.info("Application started");
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.INFO, "App Started");
           Crawler crawler= Crawler.getInstance();
           crawler.run(Arrays.asList(new String[]{"http://i.stack.imgur.com/ILTQq.png"}), false);
            
        } catch (Exception ex) {
            logger.error(ex);
        }
    }
}
