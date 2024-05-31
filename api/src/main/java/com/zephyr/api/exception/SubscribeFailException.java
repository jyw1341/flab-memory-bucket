package com.zephyr.api.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

public class SubscribeFailException extends BaseException {

    public SubscribeFailException(MessageSource messageSource) {
        super(messageSource.getMessage("duplicated.albumMember.approved", null, Locale.KOREA));
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
