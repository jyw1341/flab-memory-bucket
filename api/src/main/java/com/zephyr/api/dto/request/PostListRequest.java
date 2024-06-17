package com.zephyr.api.dto.request;

import lombok.Data;

@Data
public class PostListRequest {

    private final Long albumId;
    private final String username;
    private final String title;
    private final String seriesName;
    private final Integer page;
    private final Integer size;
}
