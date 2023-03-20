package utils;

public class FieldContentUtils {
    private FieldContentUtils() {}
    public static String format(String fieldValue) {
        return (CheckUtils.isEmpty(fieldValue)) ? "<em class=\"td_incorrect_content\">ПОЛЕ НЕ ЗАПОЛНЕНО</em>" : fieldValue;
    }
    public static String format(String fieldValue, String defaultContent) {
        return (CheckUtils.isEmpty(fieldValue)) ? defaultContent : fieldValue;
    }
}
