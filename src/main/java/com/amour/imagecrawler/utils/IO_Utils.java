/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amour.imagecrawler.utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amour
 */
public class IO_Utils {

    /**
     * Verify it the directory exist
     * @param dirName The name of the directory
     * @return True if exist otherwise False
     */
    public static Boolean directoryExit(String dirName) {
        File destDir = new File(dirName);
        return destDir.exists();
    }

    /**
     * Retrieve the given file extension
     * @param file The given file
     * @return the file extension
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    /**
     * Create directory on the disk
     * @param dirName The name of the directory
     */
    public static void createDirectory(String dirName) {
        
        File destDir = new File(dirName);
        try {
            destDir.mkdir();
            
        } catch (SecurityException se) {
            Logger.getLogger(IO_Utils.class.getName()).log(Level.SEVERE, 
                    "Filed to create directory on disk",se);
            throw se;
        }
    }

    /**
     * Combine several paths into one
     * @param paths The given paths
     * @return Concatenate path
     */
    public static String pathCombine(String... paths) {
        File file = new File(paths[0]);
        for (int i = 1; i < paths.length; i++) {
            file = new File(file, paths[i]);
        }
        return file.getPath();
    }
}