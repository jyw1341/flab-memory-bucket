package com.zephyr.api.utils;

public class HttpRequestUtils {

    public static String createUrl(int port, String path) {
        return String.format("http://localhost:%d/%s", port, path);
    }
}
