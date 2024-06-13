package com.zephyr.api.dto;

import lombok.Data;

@Data
public class PostSearchDTO {

    private final Long albumId;
    private final Long memberId;
    private final String username;
    private final String title;
    private final String seriesName;
    private final Integer page;
    private final Integer size;
}
