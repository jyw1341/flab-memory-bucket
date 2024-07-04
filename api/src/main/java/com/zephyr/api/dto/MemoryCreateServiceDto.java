package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemoryCreateServiceDto {

    private String caption;
    private String contentUrl;
    private Double index;
}
