package utils;

import java.util.Collection;

/**
 * Проверка json файлов.
 */
public class CheckUtils {

    /**
     * Проверка строки на пустоту.
     *
     * @param str проверяемая строка
     * @return true, если строка null или пустая; иначе false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    /**
     * Проверка списка на пустоту.
     *
     * @param collection проверяемая коллекция
     * @return true, если список null или пустой; иначе false
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }
}