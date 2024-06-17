package com.zephyr.api.dto;

import lombok.Data;

@Data
public class AlbumUpdateServiceDto {

    private final Long albumId;
    private final String memberId;
    private final String title;
    private final String description;
    private final String thumbnailUrl;

}
