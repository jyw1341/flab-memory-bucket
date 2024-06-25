package com.zephyr.api.utils;

public class TestStringUtils {

    public static String createUrl(int port, String path) {
        return String.format("http://localhost:%d/%s", port, path);
    }

    public static String convertToUnderscore(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(camelCase.charAt(0)));

        for (int i = 1; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_').append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }
}
