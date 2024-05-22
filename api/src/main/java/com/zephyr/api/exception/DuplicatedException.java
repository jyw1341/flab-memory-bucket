package com.zephyr.api.exception;

import org.springframework.http.HttpStatus;

public class DuplicatedException extends BaseException {

    public DuplicatedException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
