package com.zephyr.api.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

public class InvalidRequestException extends BaseException {

    public InvalidRequestException(MessageSource messageSource) {
        super(messageSource.getMessage("invalid", null, Locale.KOREA));
    }

    public InvalidRequestException(String fieldName, String message) {
        super(message);
        addValidation(fieldName, message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
