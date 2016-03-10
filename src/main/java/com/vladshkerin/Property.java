package com.vladshkerin;

import com.vladshkerin.exception.NotFoundSettingException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * Класс для сохранения и восстановления параметров программы из файла.
 *
 * @author Vladimir Shkerin
 */
public class Property implements Settings {

    private static final Property INSTANCE;

    private static Properties properties;
    private static File propertiesFile;

    static {
        INSTANCE = new Property();

        String userDir = System.getProperty("user.home");
        File propertiesDir = new File(userDir, ".launcher1c");
        if (!propertiesDir.exists()) propertiesDir.mkdir();
        propertiesFile = new File(propertiesDir, FILE_NAME_SETTINGS);

        properties = new Properties(getDefaultSettings());
        if (propertiesFile.exists()) {
            FileInputStream in;
            try {
                in = new FileInputStream(propertiesFile);
                properties.load(in);
            } catch (IOException e) {
                // TODO empty
            }
        }
    }

    /**
     * Пустой конструктор одиночки.
     */
    private Property() {
        // TODO empty
    }

    /**
     * Возврат экземпляра класса одиночки.
     *
     * @return экземпляр класса.
     */
    public static Property getInstance() {
        return INSTANCE;
    }

    /**
     * Формирует и возвращает параметры приложения по умолчанию.
     *
     * @return переменная типа Property с установленными по умолчанию параметрами
     */
    private static Properties getDefaultSettings() {
        Properties defProperty = new Properties();

        String strDateBackup = new SimpleDateFormat("MM_yyyy").format(System.currentTimeMillis());
        Calendar calendar = GregorianCalendar.getInstance(Resource.getCurrentLocale());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String strDateUnload = new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime());

        defProperty.put("width.size.window", "450");
        defProperty.put("height.size.window", "350");
        defProperty.put("width.position.window", "30");
        defProperty.put("height.position.window", "30");
        defProperty.put("path.1c", Path.getPath1cDefault());
        defProperty.put("path.base", "C:\\base1c");
        defProperty.put("path.backup", "C:\\backup");
        defProperty.put("file.1c", "1cv8.exe");
        defProperty.put("file.test", "chdbfl.exe");
        defProperty.put("file.backup", "base1c_" + strDateBackup);
        defProperty.put("last.date.unload_db", strDateUnload);

        return defProperty;
    }

    /**
     * Записывает параметры в выходной поток.
     *
     * @throws IOException если возникает ошибка записи параметров в выходной поток
     */
    @Override
    public void storeSettings() throws IOException {
        FileOutputStream out = new FileOutputStream(propertiesFile);
        properties.store(out, "Program settings");
    }

    /**
     * Возвращает строку параметра приложения.
     *
     * @param key ключ для поиска
     * @return строку с найденным параметром
     * @throws NotFoundSettingException если параметр не найден по ключу key
     */
    @Override
    public String getString(String key) throws NotFoundSettingException {
        String property = properties.getProperty(key);
        if (property == null) {
            throw new NotFoundSettingException("settings \"" + key + "\" not found");
        }
        return property;
    }

    /**
     * Устанавливает параметр приложения.
     *
     * @param key   строковая переменная для установки ключа
     * @param value значение параметра
     */
    @Override
    public void setSetting(String key, String value) {
        properties.setProperty(key, value);
    }

}
