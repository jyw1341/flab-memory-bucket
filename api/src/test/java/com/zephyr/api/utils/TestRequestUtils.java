package com.zephyr.api.utils;

public class TestRequestUtils {

    public static String createUrl(int port, String path) {
        return String.format("http://localhost:%d/%s", port, path);
    }
}
