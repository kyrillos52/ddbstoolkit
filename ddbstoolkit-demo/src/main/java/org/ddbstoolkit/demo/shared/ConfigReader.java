package org.ddbstoolkit.demo.shared;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Class to read a configuration file
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class ConfigReader {


    /**
     * Properties
     */
    private Properties properties;

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public ConfigReader(String filename) {

        try {
            properties = new Properties();
            String userDir = System.getProperty("user.dir");
            properties.load(new FileInputStream(userDir + "/"+filename));

            System.out.println("Properties loaded");
        }
        catch (Exception e) {
            System.out.println("Fail to load properties");
        }
    }


}
