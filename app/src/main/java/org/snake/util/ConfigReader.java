package org.snake.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties prop = new Properties();

    public static void readConfig(String configFilename) {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(configFilename)) {
            if (is == null) {
                throw new FileNotFoundException("The properties file " + configFilename + " does not exist.");
            }

            // load the properties file
            prop.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String property) {
        return prop.getProperty(property);
    }
}
