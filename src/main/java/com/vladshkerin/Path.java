package com.vladshkerin;

import com.vladshkerin.exception.NotFoundSettingException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с путями 1С.
 *
 * @author Vladimir Shkerin
 */
public class Path {

    private static Settings settings = Property.getInstance();

    /**
     * Проверяет существование всех путей для указанной операции.
     *
     * @param operation операция для вычисления проверяемых путей
     * @return пустую переменную типа List<String> если все пути найдены;
     * List<String> содержащий все ненайденные пути.
     */
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
                String path = settings.getString(setting);
                if (!checkPath(path)) {
                    errorList.add(Resource.getString("strPathNotFound") + ": " + path + "\n");
                }
            } catch (NotFoundSettingException e) {
                errorList.add(Resource.getString("strSettingNotFound") + ": " + setting + "\n");
            }
        }

        return errorList;
    }

    /**
     * Проверяет существование указанного пути.
     *
     * @param path проверяемый путь.
     * @return true если путь существует;
     * false если путь не существует.
     */
    public static boolean checkPath(String path) {
        return new File(path).exists();
    }

    /**
     * Возвращает путь к программе 1С.
     *
     * @return если путь к программе 1С найден, строку с найденным путём;
     * если путь не найден, строку со значением "C:\\Program Files\\1cv82\\8.2.19.90\\bin\\"
     */
    public static String getPath1cDefault() {
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
