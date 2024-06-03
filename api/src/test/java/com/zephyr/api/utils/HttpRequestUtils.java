package com.zephyr.api.utils;

public class HttpRequestUtils {

    public static final String BASE_URL = "http://localhost:";

    public static String createUrl(int port, String path) {
        return BASE_URL + port + path;
    }
}
