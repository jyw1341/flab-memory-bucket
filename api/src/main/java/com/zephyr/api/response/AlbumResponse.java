package com.zephyr.api.response;

import com.zephyr.api.domain.Album;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String thumbnailUrl;
    private final LocalDateTime created;

    public AlbumResponse(Album album) {
        this.id = album.getId();
        this.title = album.getTitle();
        this.description = album.getDescription();
        this.thumbnailUrl = album.getThumbnailUrl();
        this.created = album.getCreated();
    }
}
