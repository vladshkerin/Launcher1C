package com.vladshkerin;

import java.awt.*;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Класс для ресурсов программы.
 *
 * @author Vladimir Shkerin
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

    /**
     * Пустой конструктор.
     */
    private Resource() {
        //TODO empty
    }

    /**
     * Возвращает строку ресурса.
     *
     * @param key ключ для поиска ресурса
     * @return строка ресурса найденная по ключу key
     */
    public static String getString(String key) {
        if ("Application.version".equals(key)) {
            return resourceVer.getString(key);
        } else {
            return resourceStr.getString(key);
        }
    }

    /**
     * Возвращает текущую местность.
     *
     * @return переменная типа Local с текущей местностью
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Возвращает текущий путь программы.
     *
     * @return строка с текущим путём программы.
     */
    public static String getCurrentPath() {
        return currentPath;
    }

    /**
     * Возвращает текущий шрифт программы.
     *
     * @return переменная типа Font с текущим шрифтом программы.
     */
    public static Font getCurrentFont() {
        return currentFont;
    }
}
