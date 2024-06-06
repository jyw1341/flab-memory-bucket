package com.zephyr.api.request;

import lombok.Data;

@Data
public class AlbumUpdate {
    private final String title;
    private final String description;
    private final String thumbnailUrl;

    public AlbumUpdate(String title, String description, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }
}
