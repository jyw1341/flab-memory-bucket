package com.zephyr.api.dto;

import lombok.Data;

@Data
public class AlbumUpdateDTO {

    private final Long albumId;
    private final Long memberId;
    private final String title;
    private final String description;
    private final String thumbnailUrl;

}
