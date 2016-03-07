package com.vladshkerin;

import com.vladshkerin.exception.NotFoundSettingException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class is used to get or set settings program.
 */
public class Settings {

    private static final String FILE_NAME_SETTINGS = "settings.ini";

    private static File propertiesFile;
    private static Properties settings;

    static {
        String userDir = System.getProperty("user.home");
        File propertiesDir = new File(userDir, ".launcher1c");
        if (!propertiesDir.exists())
            propertiesDir.mkdir();
        propertiesFile = new File(propertiesDir, FILE_NAME_SETTINGS);

        try {
            initSettings();
        } catch (IOException e) {
            // empty
        }
    }

    private Settings() {
        // TODO empty
    }

    public static void initSettings() throws IOException {
        Properties defaultSettings = getDefaultSettings();
        settings = new Properties(defaultSettings);

        if (propertiesFile.exists()) {
            FileInputStream in = new FileInputStream(propertiesFile);
            settings.load(in);
        }
    }

    private static Properties getDefaultSettings() {
        String strDateBackup = new SimpleDateFormat("MM_yyyy").format(System.currentTimeMillis());
        Calendar calendar = GregorianCalendar.getInstance(Resource.getCurrentLocale());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String strDateUnload = new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime());

        Properties defaultSettings = new Properties();
        defaultSettings.put("width.size.window", "450");
        defaultSettings.put("height.size.window", "350");
        defaultSettings.put("width.position.window", "30");
        defaultSettings.put("height.position.window", "30");
        defaultSettings.put("path.1c", getPath1cDefault());
        defaultSettings.put("path.base", "C:\\base1c");
        defaultSettings.put("path.backup", "C:\\backup");
        defaultSettings.put("file.1c", "1cv8.exe");
        defaultSettings.put("file.test", "chdbfl.exe");
        defaultSettings.put("file.backup", "base1c_" + strDateBackup);
        defaultSettings.put("last.date.unload_db", strDateUnload);
        return defaultSettings;
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

    public static void storeSettings() throws IOException {
        FileOutputStream out = new FileOutputStream(propertiesFile);
        settings.store(out, "Program settings");
    }

    public static String getString(String key) throws NotFoundSettingException {
        String property = settings.getProperty(key);
        if (property == null) {
            throw new NotFoundSettingException("settings \"" + key + "\" not found");
        }
        return property;
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
}
