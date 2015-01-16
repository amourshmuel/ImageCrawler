/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amour.imagecrawler;

import com.amour.imagecrawler.dal.ImagesJpaController;
import com.amour.imagecrawler.utils.IO_Utils;
import com.amour.imagecrawler.dal.Images;
import com.amour.imagecrawler.plugins.ImagePlugin;
import com.amour.imagecrawler.utils.Hashing;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author amour
 */
public class ImagesManager {
    
    private static ImagesManager instance;
    private final List<String> imagesList;
    private List<ImagePlugin> imagePlugins;
    private final ImagesJpaController jpaController;

    protected ImagesManager() throws SQLException {
        
        this.imagesList = new ArrayList<>();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpaManager");
        jpaController=new ImagesJpaController(emf);
    }
    
    public static ImagesManager getInstance() 
            throws SQLException {
        if(instance == null) {
            instance = new ImagesManager();
        }
        return instance;
    }

    /**
     * Load images from file
     * @param propertiesManager The Properties-Manager
     * @throws IOException 
     */
    public void loadImages(Properties propertiesManager) 
            throws IOException {
        File file = new File(propertiesManager.getProperty(Crawler.IMAGES_REPOSITORY_FILE_KEY));
        readImagesFile(file);
    }
    
    /**
     * Load images Urls from external collection
     * @param externalImagesUrl The image Urls to load
     */
    public void loadExternalImages(List<String> externalImagesUrl) {
        for(String imageUrl: externalImagesUrl){
            if (!this.imagesList.contains(imageUrl)) {
                this.imagesList.add(imageUrl);
            } else {
                Logger.getLogger(Crawler.class.getName()).log(Level.WARNING, "Dupplicate entry");
            }
        }
    }
    
    /**
     * Clear the existing images list
     */
     public void ClgearImageList() {
         
        if(imagesList!=null)
            imagesList.clear();
     }

    /**
     * Run crawler operation in multi-thread
     * @param propertiesManager The Properties-Manager
     * @throws IOException 
     * @throws java.security.NoSuchAlgorithmException 
     */
    public void run(Properties propertiesManager) throws IOException, 
            NoSuchAlgorithmException, Exception{

         ExecutorService executor = Executors.newFixedThreadPool(
                 Integer.parseInt(propertiesManager.getProperty(Crawler.NUMBER_OF_WORKER_THREADS_KEY)));
         for (String imageUrl : this.imagesList) {
             
             Runnable worker = new ImageRunable(imageUrl, propertiesManager);
             executor.execute(worker);
           }
           executor.shutdown();
           while (!executor.isTerminated()) {
           }
    }
    
    private class ImageRunable implements Runnable{

        private final String imageUrl;
        private final Properties propertiesManager;
        ImageRunable(String imageUrl, Properties propertiesManager){
            
            this.imageUrl=imageUrl;
            this.propertiesManager=propertiesManager;
        }
        
        @Override
        public void run() {
            
            try {
                Images imagedetails=new Images();
                imagedetails.setImageurl(imageUrl.getBytes());
                imagedetails.setImagechecksum(Hashing.toMD5(imageUrl));
                saveImage(imageUrl, propertiesManager.getProperty(Crawler.LOCAL_REPOSITORY_FOLDER_KEY),imagedetails);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(ImagesManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ImagesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Using Spring IoC to load the Images plug-ins
     * @param propertiesManager The Properties-Manager
     */
    public void loadPlugins(Properties propertiesManager) {
        
        imagePlugins=new ArrayList<>();
        String pulugins=propertiesManager.getProperty(Crawler.IMAGES_PLUGINS_TO_LOAD_KEY);
        
        Map pluginsMap=new HashMap<>();
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        for(String pluginKey :pulugins.split(",")){
            
            if(!pluginsMap.containsKey(pluginKey)){
                if(!context.containsBean(pluginKey)){
                    
                    Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, "Plugin not exist");
                    continue;
                }
                Object plugin=context.getBean(pluginKey);
                if (plugin instanceof ImagePlugin) {
                    
                    imagePlugins.add((ImagePlugin)plugin);
                }
                pluginsMap.put(pluginKey, pluginKey);
            }
        }
    }
    
    
    private void readImagesFile(File fin) throws IOException {
	FileInputStream fis = new FileInputStream(fin);
 
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
            String imageUrl;
            while ((imageUrl = br.readLine()) != null) {
                if (!this.imagesList.contains(imageUrl)) {
                    this.imagesList.add(imageUrl);
                } else {
                    Logger.getLogger(Crawler.class.getName()).log(Level.WARNING, "Dupplicate entry");
                }
                
            }
        }
}
    
    private void saveImage(String imageUrl, String destinationFileDir,Images imagedetails) throws IOException, Exception {
        BufferedImage imBuff = downloadImage(imageUrl,imagedetails);
        
        imBuff = exceutePlugins(imBuff);
        
        saveFileToDisk(imageUrl, destinationFileDir, imagedetails, imBuff);
        
        persistToDB(imagedetails);
    }

    private void persistToDB(Images imagedetails) throws Exception {
       // jpaController.
        jpaController.create(imagedetails);
       Object g= jpaController.findImagesEntities();
    }

    private void saveFileToDisk(String imageUrl, String destinationFileDir, Images imagedetails, BufferedImage imBuff) throws IOException {
        String fileName = FilenameUtils.getName(imageUrl);
       /* Path path = Paths.get(imageUrl);
        String fileName = path.getFileName().toString();*/
        String destinationFile = IO_Utils.pathCombine(destinationFileDir, fileName);
        imagedetails.setImageondisk(destinationFile.getBytes());
        File outputfile = new File(destinationFile);
        ImageIO.write(imBuff, IO_Utils.getFileExtension(outputfile), outputfile);
    }

    private BufferedImage exceutePlugins(BufferedImage imBuff) {
        for(ImagePlugin plug : imagePlugins){
            
            System.out.print(plug.getPluginName());
            imBuff = plug.run(imBuff);
        }
        return imBuff;
    }

    private BufferedImage downloadImage(String imageUrl, Images imagedetails) throws MalformedURLException, IOException {
        URL urlImage = new URL(imageUrl);
        BufferedImage imBuff = ImageIO.read(urlImage);
        imagedetails.setDownloadtime(new Date());
        return imBuff;
    }
}