package com.zephyr.api.exception;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseException extends RuntimeException {

    private final Map<String, String> validation = new HashMap<>();

    public BaseException(String message) {
        super(message);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
