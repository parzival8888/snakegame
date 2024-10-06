package org.snake.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A utility class for reading configuration properties from a specified properties file.
 * This class provides methods to load the properties and retrieve specific property values
 * needed for configuring the Snake game.
 */
public class ConfigReader {
    private static Properties prop = new Properties(); // Properties object to hold configuration values

    /**
     * Reads configuration properties from the specified file.
     *
     * @param configFilename The name of the properties file to read.
     * @throws FileNotFoundException If the specified properties file does not exist.
     */
    public static void readConfig(String configFilename) {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(configFilename)) {
            if (is == null) {
                throw new FileNotFoundException("The properties file " + configFilename + " does not exist.");
            }

            // Load the properties file into the Properties object
            prop.load(is);
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if an I/O error occurs
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
}