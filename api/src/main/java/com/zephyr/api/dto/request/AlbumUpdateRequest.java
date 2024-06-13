package com.zephyr.api.dto.request;

import lombok.Data;

@Data
public class AlbumUpdateRequest {
    private final String title;
    private final String description;
    private final String thumbnailUrl;
    private Long albumId;
    private Long ownerId;
}
