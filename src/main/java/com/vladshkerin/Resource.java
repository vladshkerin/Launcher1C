package com.vladshkerin;

import java.awt.*;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class is used to get or set resources program.
 */
public class Resource {

    private static ResourceBundle resourceStr;
    private static ResourceBundle resourceVer;
    private static Locale currentLocale;
    private static String currentPath;
    private static Font currentFont;

    static {
        String language = System.getProperties().getProperty("user.language");
        String country = System.getProperties().getProperty("user.country");

        currentLocale = new Locale(language, country);
        currentPath = new File("").getAbsolutePath();
        resourceStr = ResourceBundle.getBundle("strings", currentLocale);
        resourceVer = ResourceBundle.getBundle("buildNumber");
        currentFont = new Font("Arial", Font.PLAIN, 12);//UIManager.getFont("List.font")
    }

    private Resource() {
        //TODO empty
    }

    public static String getString(String key) {
        if ("Application.version".equals(key)) {
            return resourceVer.getString(key);
        } else {
            return resourceStr.getString(key);
        }
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static String getCurrentPath() {
        return currentPath;
    }

    public static Font getCurrentFont() {
        return currentFont;
    }
}
