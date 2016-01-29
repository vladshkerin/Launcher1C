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
    private static Properties settings;
    private static String workPathProgram;

    static {
        workPathProgram = new File("").getAbsolutePath();
        initSettings();
    }

    private Settings() {
        // TODO empty
    }

    public static void initSettings() {
        File file = new File(workPathProgram + File.separator + FILE_NAME_SETTINGS);
        settings = getSettings(file);
    }

    public static void storeSettings() throws IOException {
        File file = new File(workPathProgram + File.separator + FILE_NAME_SETTINGS);
        settings.store(new FileWriter(file), FILE_NAME_SETTINGS);
    }

    public static String getString(String key) throws NotFoundSettingException {
        String property = settings.getProperty(key);
        if (property == null) {
            return getStringSettingDefault(key);
        }
        return property;
    }

    public static void setSettings(Map<String, String> map) {
        if (map.isEmpty()) return;
        settings.putAll(map);
    }

    public static void setSetting(String key, String value) {
        settings.setProperty(key, value);
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

    private static Properties getSettings(File file) {
        Properties settings = new java.util.Properties();
        try {
            if (file.exists()) {
                settings.load(new FileReader(file));
                checkFillSettings(settings);
            } else {
                if (file.createNewFile()) {
                    settings.putAll(getMapSettingsDefault());
                    settings.store(new FileWriter(file), FILE_NAME_SETTINGS);
                } else {
                    throw new IOException();
                }
            }
        } catch (IOException e) {
            settings = new java.util.Properties();
            log.log(Level.SEVERE, "Not found file settings: " + file.getAbsolutePath());
        }

        return settings;
    }

    private static void checkFillSettings(Properties settings) {
        Map<String, String> mapSettingsDefault = getMapSettingsDefault();
        for (Map.Entry<Object, Object> entry : settings.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            mapSettingsDefault.put(key, value);
        }
        settings.putAll(mapSettingsDefault);
    }

    private static Map<String, String> getMapSettingsDefault() {
        Map<String, String> mapSettings = new LinkedHashMap<>();
        for (String str : getListSettings()) {
            try {
                mapSettings.put(str, getStringSettingDefault(str));
            } catch (NotFoundSettingException e) {
                log.log(Level.CONFIG, e.getMessage());
            }
        }
        return mapSettings;
    }

    private static List<String> getListSettings() {
        List<String> list = new ArrayList<>();
        list.add("width.size.window");
        list.add("height.size.window");
        list.add("width.position.window");
        list.add("height.position.window");
        list.add("path.1c");
        list.add("path.base");
        list.add("path.backup");
        list.add("file.1c");
        list.add("file.test");
        list.add("file.backup");
        list.add("last.date.unload_db");
        return list;
    }

    private static String getStringSettingDefault(String key) throws NotFoundSettingException {
        switch (key) {
            case "width.size.window":
                return "450";
            case "height.size.window":
                return "350";
            case "width.position.window":
                return "30";
            case "height.position.window":
                return "30";
            case "path.1c":
                return getPath1cDefault();
            case "path.base":
                return "C:\\base1c";
            case "path.backup":
                return "C:\\backup";
            case "file.1c":
                return "1cv8.exe";
            case "file.test":
                return "chdbfl.exe";
            case "file.backup":
                return "base1c_" +
                        new SimpleDateFormat("MM_yyyy").format(System.currentTimeMillis());
            case "last.date.unload_db":
                Calendar calendar = GregorianCalendar.getInstance(Resource.getCurrentLocale());
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                return new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime());
            default:
                throw new NotFoundSettingException("Not found property: " + key);
        }
    }

    private static String getPath1cDefault() {
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
