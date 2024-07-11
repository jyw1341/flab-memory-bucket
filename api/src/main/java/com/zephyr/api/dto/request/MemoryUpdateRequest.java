package com.zephyr.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemoryUpdateRequest {

    private final Long id;
    private final Double index;
    private final String caption;
    private final String contentUrl;
}
