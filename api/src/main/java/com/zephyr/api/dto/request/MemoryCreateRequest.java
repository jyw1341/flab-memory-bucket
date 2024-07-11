package com.zephyr.api.dto.request;

import com.zephyr.api.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemoryCreateRequest {

    private final String requestId;
    private final ContentType contentType;
    private final Double index;
    private final String contentUrl;
    private final String caption;
}
