package com.zephyr.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemoryCreateRequest {

    private final Double index;
    private final String contentUrl;
    private final String caption;
}
