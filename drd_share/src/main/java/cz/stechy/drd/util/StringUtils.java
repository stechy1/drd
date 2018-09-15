package cz.stechy.drd.util;

public class StringUtils {

    private StringUtils() {
        throw new IllegalStateException();
    }

    public static String capitalizeFirst(String text) {
        return text.substring(0,1).toUpperCase() + text.substring(1);
    }

    public static String lowerFirst(String text) {
        return text.substring(0,1).toLowerCase() + text.substring(1);
    }

    private static String convertToCamel(String text, String separator, boolean lowerFirst) {
        String result = text.replaceAll(" ", "");
        if (lowerFirst) {
            result = lowerFirst(result);
        }

        String[] parts = result.split(separator);
        StringBuilder camelCaseString = new StringBuilder();
        for (String part : parts){
            camelCaseString.append(capitalizeFirst(part));
        }

        return camelCaseString.toString();
    }

    private static String convertFromCamel(String text, String separator) {
        return text.replaceAll("([^_A-Z])([A-Z])", "$1" + separator + "$2");
    }

    public static String hyphensToCamel(String text, boolean lowerFirst) {
        return convertToCamel(text, "-", lowerFirst);
    }

    public static String camelToHyphens(String text) {
        return convertFromCamel(text, "-");
    }

    public static String snakeToCamel(String text, boolean lowerFirst) {
        return convertToCamel(text, "_", lowerFirst);
    }

    public static String camelToSnake(String text) {
        return convertFromCamel(text, "_");
    }
}
