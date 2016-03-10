package com.vladshkerin;

import com.vladshkerin.exception.NotFoundSettingException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Класс для сохранения и восстановления параметров программы из регистра.
 *
 * @author Vladimir Shkerin
 */
public class Preference implements Settings {

    private static final Preference INSTANCE;

    private static Preferences node;

    static {
        INSTANCE = new Preference();
        node = Preferences.userNodeForPackage(INSTANCE.getClass());
        if ("".equals(node.get("width.size.window", ""))) {
            node = getDefaultSettings();
        }
    }

    /**
     * Возвращает экземпляр класса одиночки.
     *
     * @return экземпляр класса.
     */
    public static Preference getInstance() {
        return INSTANCE;
    }

    /**
     * Формирует и возвращает параметры приложения по умолчанию.
     *
     * @return переменная типа Property с установленными по умолчанию параметрами
     */
    private static Preferences getDefaultSettings() {
        Preferences defNode = Preferences.userNodeForPackage(INSTANCE.getClass());

        String strDateBackup = new SimpleDateFormat("MM_yyyy").format(System.currentTimeMillis());
        Calendar calendar = GregorianCalendar.getInstance(Resource.getCurrentLocale());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String strDateUnload = new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime());

        defNode.put("width.size.window", "450");
        defNode.put("height.size.window", "350");
        defNode.put("width.position.window", "30");
        defNode.put("height.position.window", "30");
        defNode.put("path.1c", Path.getPath1cDefault());
        defNode.put("path.base", "C:\\base1c\\");
        defNode.put("path.backup", "C:\\backup\\");
        defNode.put("file.1c", "1cv8.exe");
        defNode.put("file.test", "chdbfl.exe");
        defNode.put("file.backup", "base1c_" + strDateBackup);
        defNode.put("last.date.unload_db", strDateUnload);

        return defNode;
    }

    /**
     * Сохраняет параметры в регистр.
     *
     * @throws IOException если возникает ошибка записи параметров в регистр
     */
    @Override
    public void storeSettings() throws IOException {
        // TODO empty
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
        String value = node.get(key, "");
        if ("".equals(value))
            throw new NotFoundSettingException("settings \"" + key + "\" not found");
        return value;
    }

    /**
     * Устанавливает параметр приложения.
     *
     * @param key   строковая переменная для установки ключа
     * @param value значение параметра
     */
    @Override
    public void setSetting(String key, String value) {
        node.put(key, value);
    }
}
