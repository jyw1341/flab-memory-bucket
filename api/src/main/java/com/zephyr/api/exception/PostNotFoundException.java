package com.zephyr.api.exception;

import org.springframework.http.HttpStatus;

public class PostNotFoundException extends BaseException {

    public static final String MESSAGE = "포스트를 찾을 수 없습니다.";

    public PostNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
