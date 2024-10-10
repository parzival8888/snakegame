package org.snake.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class for reading configuration properties from a specified
 * properties file.
 * 
 * This class provides methods to load the properties and retrieve specific
 * property values needed for configuring the Snake game. It also enables
 * the property file to be updated.
 */
public class ConfigReader {
    private static final Logger LOGGER = Logger.getLogger(ConfigReader.class.getName());
    private static Properties prop = new Properties(); // Properties object to hold configuration values

    /**
     * Reads configuration properties from the specified file. The file is
     * read from the classpath, which is useful when the application is packaged
     * into a JAR. However, this makes the property file read only. This method
     * creates a copy of the property file and stores it locally so that any
     * changes to the property file can be saved and re-retrieved.
     *
     * @param configFilename The name of the properties file to read.
     */
    public static void readConfig(String configFilename) {
        // Try loading the external configuration file first
        File localFile = new File(configFilename);

        // If the local property file doesn't exist, copy it from the JAR (classpath)
        if (!localFile.exists()) {
            try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(configFilename)) {
                if (is == null) {
                    throw new FileNotFoundException("The properties file " + configFilename + " does not exist.");
                }
                // Copy the properties file to a local property file
                FileOutputStream outputStream = new FileOutputStream(localFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
            } catch (FileNotFoundException e) {
                LOGGER.log(Level.SEVERE, "Config file not found: " + configFilename, e);
                e.printStackTrace();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "I/O error when reading config file: " + configFilename, e);
                e.printStackTrace();
            }
        }

        // Now load the local properties file
        try (FileInputStream fileInput = new FileInputStream(localFile)) {
            prop.load(fileInput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the value of a specified property from the loaded properties.
     *
     * @param property The name of the property to retrieve.
     * @return The value of the specified property, or null if not found.
     */
    public static String getProperty(String property) {
        return prop.getProperty(property); // Return the value associated with the property name
    }

    /**
     * Retrieves all properties from the loaded properties file.
     *
     * @return A two-dimensional array containing all properties and their values.
     */
    public static Object[][] getAllProperties() {
        int numberOfProperties = prop.size();

        // Create data array for the properties
        Object[][] data = new Object[numberOfProperties][2];

        int index = 0;
        for (String key : prop.stringPropertyNames()) {
            String value = prop.getProperty(key);
            data[index][0] = key;
            data[index][1] = value;
            index++;
        }
        return data;
    }

    /**
     * Save properties back to the properties file. This method saves to a local
     * properties file. If the application was packaged into a JAR, the original 
     * properties file becomes read-only, so this method updates the copy made 
     * in readConfig above.
     *
     * @param properties A two-dimensional array containing all properties and their
     *                   values.
     */
    public static void saveAllProperties(String configFilename, Object[][] properties) {
        prop.clear();

        // Populate the Properties object from the 2D array
        for (Object[] entry : properties) {
            String key = (String) entry[0];
            String value = (String) entry[1];
            prop.setProperty(key, value);
        }

        try (FileOutputStream output = new FileOutputStream(configFilename)) {
            prop.store(output, "Updated Properties");
        } catch (IOException e) {
            System.out.println("Error saving properties file: " + e.getMessage());
        }
    }
}