package com.vladshkerin;

import com.vladshkerin.exception.NotFoundPropertyException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to get or set properties program.
 */
public class Settings {

    private static final String FILE_NAME_SETTINGS = "settings.ini";

    private static Logger log = Logger.getLogger(UpdateProgram.class.getName());

    private static Properties properties;
    private static String currentPath;

    static {
        currentPath = new File("").getAbsolutePath();
        File file = new File(currentPath + File.separator + FILE_NAME_SETTINGS);
        try {
            properties = getProperties(file);
        } catch (NotFoundPropertyException e) {
            properties = new java.util.Properties();
            log.log(Level.WARNING, "Not found file settings: " + file.getAbsolutePath());
        }
    }

    private Settings() {
        //TODO empty
    }

    public static String getString(String key) throws NotFoundPropertyException {
        if (properties.getProperty(key) == null) {
            throw new NotFoundPropertyException("Not found property: " + key);
        }
        return properties.getProperty(key);
    }

    private static Properties getProperties(File file) throws NotFoundPropertyException {
        Properties properties = new java.util.Properties();
        if (file.exists()) {
            try {
                properties.load(new FileReader(file));
            } catch (IOException e) {
                throw new NotFoundPropertyException(e.getMessage());
            }
        } else {
            throw new NotFoundPropertyException(
                    "Not found file property: " + file.getAbsolutePath());
        }
        return properties;
    }

    public static void setProperties(Map<String, String> map) {
        if (map.isEmpty())
            return;

        properties.putAll(map);
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public static void storeProperties() throws NotFoundPropertyException {
        File file = new File(currentPath + File.separator + FILE_NAME_SETTINGS);
        try {
            properties.store(new FileWriter(file), FILE_NAME_SETTINGS);
        } catch (IOException e) {
            throw new NotFoundPropertyException(
                    "Error store properties in file" + file.getAbsolutePath());
        }
    }
}
