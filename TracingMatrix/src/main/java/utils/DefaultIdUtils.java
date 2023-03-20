package utils;

public class DefaultIdUtils {
    private static int defaultIdsCount = 0;

    /**
     * Генерация значений по умолчанию для пустых идентификаторов объектов.
     * @return идентификатор по умолчанию для некоторого объекта
     */
    public static String getId() {
        defaultIdsCount++;
        return "default-obj-id-" + defaultIdsCount;
    }
}
