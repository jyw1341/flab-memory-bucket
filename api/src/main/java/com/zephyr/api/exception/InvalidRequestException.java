package com.zephyr.api.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends BaseException {

    private static final String Message = "Invalid request";

    public InvalidRequestException(String fieldName, String message) {
        super(Message);
        addValidation(fieldName, message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
