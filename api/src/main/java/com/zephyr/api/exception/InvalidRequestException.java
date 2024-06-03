package com.zephyr.api.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends BaseException {

    public InvalidRequestException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
