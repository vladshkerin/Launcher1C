package com.vladshkerin;

import com.vladshkerin.exception.NotFoundSettingException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Класс для параметров программы.
 *
 * @author  Vladimir Shkerin
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

    /**
     * Пустой конструктор.
     */
    private Settings() {
        // TODO empty
    }

    /**
     * Инициализирует внутреннее поля.
     *
     * @throws  IOException если возникает ошибка чтения из потока
     */
    public static void initSettings() throws IOException {
        Properties defaultSettings = getDefaultSettings();
        settings = new Properties(defaultSettings);

        if (propertiesFile.exists()) {
            FileInputStream in = new FileInputStream(propertiesFile);
            settings.load(in);
        }
    }

    /**
     * Формирует и возвращает параметры приложения по умолчанию.
     *
     * @return  переменная типа Properties с установленными по умолчанию параметрами
     */
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
        defaultSettings.put("path.1c", Path.getPath1cDefault());
        defaultSettings.put("path.base", "C:\\base1c");
        defaultSettings.put("path.backup", "C:\\backup");
        defaultSettings.put("file.1c", "1cv8.exe");
        defaultSettings.put("file.test", "chdbfl.exe");
        defaultSettings.put("file.backup", "base1c_" + strDateBackup);
        defaultSettings.put("last.date.unload_db", strDateUnload);
        return defaultSettings;
    }

    /**
     * Записывает параметры в выходной поток.
     *
     * @throws  IOException если возникает ошибка записи параметров в выходной поток
     */
    public static void storeSettings() throws IOException {
        FileOutputStream out = new FileOutputStream(propertiesFile);
        settings.store(out, "Program settings");
    }

    /**
     * Возвращает строку параметра приложения.
     *
     * @param   key     ключ для поиска
     * @return  строку с найденным параметром
     * @throws  NotFoundSettingException если параметр не найден по ключу key
     */
    public static String getString(String key) throws NotFoundSettingException {
        String property = settings.getProperty(key);
        if (property == null) {
            throw new NotFoundSettingException("settings \"" + key + "\" not found");
        }
        return property;
    }

    /**
     * Устанавливает параметр приложения.
     *
     * @param   key     строковая переменная для установки ключа
     * @param   value   значение параметра
     */
    public static void setSetting(String key, String value) {
        settings.setProperty(key, value);
    }
}
