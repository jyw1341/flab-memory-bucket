package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemoryUpdateServiceDto {

    private final Long id;
    private final Double index;
    private final String caption;
    private final String contentUrl;
}
