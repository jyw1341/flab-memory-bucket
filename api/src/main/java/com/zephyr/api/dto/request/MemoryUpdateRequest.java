package com.zephyr.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemoryUpdateRequest {

    private final Long id;
    private final String caption;
    private final Double index;
}
