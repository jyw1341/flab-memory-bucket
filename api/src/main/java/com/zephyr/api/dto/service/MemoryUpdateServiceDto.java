package com.zephyr.api.dto.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemoryUpdateServiceDto {

    private final Long id;
    private final String caption;
    private final Double index;
}
