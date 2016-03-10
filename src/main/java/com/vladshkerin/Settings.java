package com.vladshkerin;

import com.vladshkerin.exception.NotFoundSettingException;

import java.io.IOException;

/**
 * Интерфейс для параметров программы.
 *
 * @author Vladimir Shkerin
 */
public interface Settings {

    String FILE_NAME_SETTINGS = "settings.ini";

    /**
     * Записывает параметры в выходной поток.
     *
     * @throws IOException если возникает ошибка записи параметров в выходной поток
     */
    void storeSettings() throws IOException;

    /**
     * Возвращает строку параметра приложения.
     *
     * @param key ключ для поиска
     * @return строку с найденным параметром
     * @throws NotFoundSettingException если параметр не найден по ключу key
     */
    String getString(String key) throws NotFoundSettingException;

    /**
     * Устанавливает параметр приложения.
     *
     * @param key   строковая переменная для установки ключа
     * @param value значение параметра
     */
    void setSetting(String key, String value);
}
