package com.zephyr.api.dto;

import com.zephyr.api.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemoryCreateServiceDto {

    private String requestId;
    private ContentType contentType;
    private String contentUrl;
    private String caption;
    private Double index;
}
