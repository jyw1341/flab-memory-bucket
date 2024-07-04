package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostSearchServiceDto {

    private final Long albumId;
    private final String username;
    private final String title;
    private final String seriesName;
    private final Integer page;
    private final Integer size;
}
