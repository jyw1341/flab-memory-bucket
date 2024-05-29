package com.zephyr.api.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

public class ForbiddenException extends BaseException {

    public ForbiddenException(MessageSource messageSource) {
        super(messageSource.getMessage("forbidden", null, Locale.KOREA));
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.FORBIDDEN.value();
    }
}
