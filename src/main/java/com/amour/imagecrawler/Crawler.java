/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amour.imagecrawler;

import com.amour.imagecrawler.utils.IO_Utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author amour
 */
public class Crawler {
    
    public static final String  IMAGES_REPOSITORY_FILE_KEY= "ImagesRepositoryFile";
    public static final String LOCAL_REPOSITORY_FOLDER_KEY ="LocalRepositoryFolder";
    public static final String IMAGES_PLUGINS_TO_LOAD_KEY="ImagesPluginsToLoad";
    public static final String NUMBER_OF_WORKER_THREADS_KEY="NumberOfWorkerThreads";
    
    
    private final Properties propertiesManager=new Properties();
    
    
    private static Crawler instance = null;
    
    
    protected Crawler() 
            throws FileNotFoundException, IOException,
            ParserConfigurationException, SAXException, SQLException {
        
        loadProperties();
        ImagesManager.getInstance().loadImages(propertiesManager);
        ImagesManager.getInstance().loadPlugins(propertiesManager);
        
        if(!IO_Utils.directoryExit(propertiesManager.getProperty(Crawler.LOCAL_REPOSITORY_FOLDER_KEY)))
        {
            IO_Utils.createDirectory(propertiesManager.getProperty(Crawler.LOCAL_REPOSITORY_FOLDER_KEY));
        }
    }

    /**
     * Singelton: Retrieve Crawler
     * @return A new Crawler instance if not exist, otherwise the exist one
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException 
     * @throws java.sql.SQLException 
     */
    public static Crawler getInstance() 
            throws FileNotFoundException, IOException, 
            ParserConfigurationException, SAXException, SQLException {
        if(instance == null) {
            instance = new Crawler();
        }
        return instance;
    }
    
    /**
     * Run crawler operation
     * @throws IOException 
     * @throws java.security.NoSuchAlgorithmException 
     * @throws java.sql.SQLException 
     */
    public void run() throws IOException, NoSuchAlgorithmException, SQLException, Exception{
       
        ImagesManager.getInstance().run(propertiesManager);
    }
    
    /**
     * Run crawler operation
     * @param externalImagesUrl List of externals images Urls
     * @param useOnlyExternals Indicate if to use only the external Urls (Without the ones from the file)
     * @throws IOException 
     * @throws java.security.NoSuchAlgorithmException 
     * @throws java.sql.SQLException 
     */
    public void run(List<String> externalImagesUrl, boolean useOnlyExternals) throws IOException, NoSuchAlgorithmException, SQLException, Exception{
       
        if(externalImagesUrl ==null || externalImagesUrl.isEmpty())
            throw new IllegalArgumentException();    
        
        if(useOnlyExternals)
            ImagesManager.getInstance().ClgearImageList();
        ImagesManager.getInstance().loadExternalImages(externalImagesUrl);
        ImagesManager.getInstance().run(propertiesManager);
    }
    
    public Properties getPropertiesManager() {
        return this.propertiesManager;        
    }                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
    
    private void loadProperties() throws IOException {
        try (FileReader reader = new FileReader("properties.properties")) {
            propertiesManager.load(reader);
        }
    }
    
}