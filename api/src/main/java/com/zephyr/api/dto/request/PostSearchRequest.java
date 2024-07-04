package com.zephyr.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostSearchRequest {

    private final String username;
    private final String title;
    private final String seriesName;
    private final Integer page;
    private final Integer size;
}
