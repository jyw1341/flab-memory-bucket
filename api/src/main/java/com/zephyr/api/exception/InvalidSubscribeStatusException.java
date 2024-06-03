package com.zephyr.api.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

public class InvalidSubscribeStatusException extends BaseException {

    public InvalidSubscribeStatusException(MessageSource messageSource) {
        super(messageSource.getMessage("duplicated.albumMember.pending", null, Locale.KOREA));
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
