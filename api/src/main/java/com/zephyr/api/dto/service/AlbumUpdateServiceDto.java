package com.zephyr.api.dto.service;

import lombok.Data;

@Data
public class AlbumUpdateServiceDto {

    private final Long albumId;
    private final Long memberId;
    private final String title;
    private final String description;
    private final String thumbnailUrl;

}
