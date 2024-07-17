package com.zephyr.api.exception;

import org.springframework.http.HttpStatus;

public class MemoryNotFoundException extends BaseException {

    public static final String MESSAGE = "메모리를 찾을 수 없습니다.";

    public MemoryNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
