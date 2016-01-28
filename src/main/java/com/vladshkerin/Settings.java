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
        File file = new File(workPathProgram + File.separator + FILE_NAME_SETTINGS);
        settings = getSettings(file);
    }

    private Settings() {
        // TODO empty
    }

    public static String getString(String key) throws NotFoundSettingException {
        String property = settings.getProperty(key);
        if (property == null) {
            return getDefaultSetting(key);
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

    public static void storeSettings() throws IOException {
        File file = new File(workPathProgram + File.separator + FILE_NAME_SETTINGS);
        settings.store(new FileWriter(file), FILE_NAME_SETTINGS);
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
            } else {
                if (file.createNewFile()) {
                    fillSettings(settings);
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

    private static void fillSettings(Properties settings) {
        Map<String, String> mapSettings = new LinkedHashMap<>();
        try {
            mapSettings.put("width.size.window", getDefaultSetting("width.size.window"));
            mapSettings.put("height.size.window", getDefaultSetting("height.size.window"));
            mapSettings.put("width.position.window", getDefaultSetting("width.position.window"));
            mapSettings.put("height.position.window", getDefaultSetting("height.position.window"));
            mapSettings.put("path.1c", getDefaultSetting("path.1c"));
            mapSettings.put("path.base", getDefaultSetting("path.base"));
            mapSettings.put("path.backup", getDefaultSetting("path.backup"));
            mapSettings.put("file.1c", getDefaultSetting("file.1c"));
            mapSettings.put("file.test", getDefaultSetting("file.test"));
            mapSettings.put("file.backup", getDefaultSetting("file.backup"));
            mapSettings.put("last.date.unload_db",getDefaultSetting("last.date.unload_db"));
        } catch (NotFoundSettingException e) {
            log.log(Level.CONFIG, e.getMessage());
        }
        settings.putAll(mapSettings);
    }

    private static String getDefaultSetting(String key) throws NotFoundSettingException {
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
                return getDefaultPath1c();
            case "path.base":
                return "C:\\base1c";
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
