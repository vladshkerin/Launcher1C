package com.vladshkerin;

import com.vladshkerin.exception.NotFoundSettingException;

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
    private static Properties properties;
    private static String workPathProgram;

    static {
        workPathProgram = new File("").getAbsolutePath();
        File file = new File(workPathProgram + File.separator + FILE_NAME_SETTINGS);
        properties = getProperties(file);
    }

    private Settings() {
        // TODO empty
    }

    public static String getString(String key) throws NotFoundSettingException {
        if (properties.getProperty(key) == null) {
            throw new NotFoundSettingException("Not found property: " + key);
        }
        return properties.getProperty(key);
    }

    public static void setSettings(Map<String, String> map) {
        if (map.isEmpty()) return;
        properties.putAll(map);
    }

    public static void setSetting(String key, String value) {
        properties.setProperty(key, value);
    }

    public static void storeSettings() throws IOException {
        File file = new File(workPathProgram + File.separator + FILE_NAME_SETTINGS);
        properties.store(new FileWriter(file), FILE_NAME_SETTINGS);
    }

    public static boolean checkPath(String path) {
        return new File(path).exists();
    }

    public static List<String> checkPath(Operations operation) {
        List<String> settingsList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        switch (operation) {
            case ENTERPRISE:
            case CONFIG:
            case UPDATE:
            case UPGRADE:
            case TEST:
                settingsList.add("path.1c");
                settingsList.add("path.base");
                break;
            case CHECK:
                settingsList.add("path.1c");
                break;
            case UNLOAD_DB:
                settingsList.add("path.1c");
                settingsList.add("path.base");
                settingsList.add("path.backup");
                break;
        }
        for (String setting : settingsList) {
            try {
                String path = getString(setting);
                if (!checkPath(path)) {
                    errorList.add(Resource.getString("strPathNotFound") + ": " + path + "\n");
                }
            } catch (NotFoundSettingException e) {
                errorList.add(Resource.getString("strSettingNotFound") + ": " + setting + "\n");
            }
        }

        return errorList;
    }

    private static Properties getProperties(File file) {
        Properties properties = new java.util.Properties();
        try {
            if (file.exists()) {
                properties.load(new FileReader(file));
            } else {
                if (file.createNewFile()) {
                    fillFileProperties(properties);
                    properties.store(new FileWriter(file), FILE_NAME_SETTINGS);
                } else {
                    throw new IOException();
                }
            }
        } catch (IOException e) {
            properties = new java.util.Properties();
            log.log(Level.SEVERE, "Not found file settings: " + file.getAbsolutePath());
        }

        return properties;
    }

    private static void fillFileProperties(Properties properties) {
        Calendar calendar = GregorianCalendar.getInstance(Resource.getCurrentLocale());
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        Map<String, String> mapSettings = new LinkedHashMap<>();
        mapSettings.put("width.size.window", "450");
        mapSettings.put("height.size.window", "350");
        mapSettings.put("width.position.window", "30");
        mapSettings.put("height.position.window", "30");
        mapSettings.put("path.1c", getDefaultPath1c());
        mapSettings.put("path.base", "C:\\base1c");
        mapSettings.put("path.backup", "C:\\backup");
        mapSettings.put("file.1c", "1cv8.exe");
        mapSettings.put("file.test", "chdbfl.exe");
        mapSettings.put("file.backup", "base1c_" +
                new SimpleDateFormat("MM_yyyy").format(System.currentTimeMillis()));
        mapSettings.put("last.date.unload_db",
                new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime()));

        properties.putAll(mapSettings);
    }

    private static String getDefaultPath1c() {
        String defaultPath = "C:\\Program Files\\1cv82\\8.2.19.90\\bin\\";
        String[] masPath = new String[]{
                "C:\\Program Files\\1cv82\\8.2.19.130\\bin\\",
                "C:\\Program Files (x86)\\1cv82\\8.2.19.130\\bin\\",
                "C:\\Program Files\\1cv82\\8.2.19.90\\bin\\",
                "C:\\Program Files (x86)\\1cv82\\8.2.19.90\\bin\\",
                "C:\\Program Files\\1cv82\\8.2.19.83\\bin\\",
                "C:\\Program Files (x86)\\1cv82\\8.2.19.83\\bin\\",
                "C:\\Program Files\\1cv82\\8.2.19.76\\bin\\",
                "C:\\Program Files (x86)\\1cv82\\8.2.19.76\\bin\\",
                "C:\\Program Files\\1cv82\\8.2.18.109\\bin\\",
                "C:\\Program Files (x86)\\1cv82\\8.2.18.109\\bin\\",
                "C:\\Program Files\\1cv82\\8.2.17.169\\bin\\",
                "C:\\Program Files (x86)\\1cv82\\8.2.17.169\\bin\\",
                "C:\\Program Files\\1cv82\\8.2.16.362\\bin\\",
                "C:\\Program Files (x86)\\1cv82\\8.2.16.362\\bin\\",
                "C:\\Program Files\\1cv82\\8.2.15.294\\bin\\",
                "C:\\Program Files (x86)\\1cv82\\8.2.15.294\\bin\\"
        };

        for (String path : masPath) {
            if (new File(path).exists()) {
                return path;
            }
        }

        return defaultPath;
    }
}
