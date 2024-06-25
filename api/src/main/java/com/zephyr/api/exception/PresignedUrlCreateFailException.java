package com.zephyr.api.exception;

import org.springframework.http.HttpStatus;

public class PresignedUrlCreateFailException extends BaseException {

    public PresignedUrlCreateFailException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
