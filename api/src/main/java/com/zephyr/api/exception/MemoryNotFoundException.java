package com.zephyr.api.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

public class MemoryNotFoundException extends BaseException {

    public MemoryNotFoundException(MessageSource messageSource) {
        super(messageSource.getMessage("notFound", null, Locale.KOREA));
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
