package com.zephyr.api.dto;

import lombok.Data;

@Data
public class AlbumCreateServiceDto {

    private final Long memberId;
    private final String title;
    private final String description;
    private final String thumbnailUrl;

}
