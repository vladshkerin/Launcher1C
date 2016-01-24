package com.vladshkerin;

import com.vladshkerin.exception.NotFoundPropertyException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to get or set settings program.
 */
public class Settings {

    private static final String FILE_NAME_SETTINGS = "settings.ini";

    private static Logger log = Logger.getLogger(Settings.class.getName());
    private static String currentPath;
    private static Properties properties;

    static {
        currentPath = new File("").getAbsolutePath();
        File file = new File(currentPath + File.separator + FILE_NAME_SETTINGS);
        properties = getNewProperties(file);
    }

    private Settings() {
        //TODO empty
    }

    public static void setProperties(Map<String, String> map) {
        if (map.isEmpty()) return;
        properties.putAll(map);
    }

    public static String getString(String key) throws NotFoundPropertyException {
        if (properties.getProperty(key) == null) {
            throw new NotFoundPropertyException("Not found property: " + key);
        }
        return properties.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public static void storeProperties() throws IOException {
        File file = new File(currentPath + File.separator + FILE_NAME_SETTINGS);
        properties.store(new FileWriter(file), FILE_NAME_SETTINGS);
    }

    private static Properties getNewProperties(File file) {
        Properties newProperties = new java.util.Properties();
        try {
            if (file.exists()) {
                newProperties.load(new FileReader(file));
            } else {
                if (file.createNewFile()) {
                    fillFileProperties(newProperties);
                    newProperties.store(new FileWriter(file), FILE_NAME_SETTINGS);
                } else {
                    throw new IOException();
                }
            }
        } catch (IOException e) {
            newProperties = new java.util.Properties();
            log.log(Level.SEVERE, "Not found file settings: " + file.getAbsolutePath());
        }

        return newProperties;
    }

    private static void fillFileProperties(Properties properties) {
        Map<String, String> mapSettings = new LinkedHashMap<>();
        mapSettings.put("width.size.window", "450");
        mapSettings.put("height.size.window", "300");
        mapSettings.put("width.position.window", "30");
        mapSettings.put("height.position.window", "30");

        Calendar calendar = GregorianCalendar.getInstance(Resource.getCurrentLocale());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        mapSettings.put("last.date.unload_db",
                new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime()));

        properties.putAll(mapSettings);
    }
}
