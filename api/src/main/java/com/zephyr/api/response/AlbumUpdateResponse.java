package com.zephyr.api.response;

import com.zephyr.api.domain.Album;
import lombok.Data;

@Data
public class AlbumUpdateResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String thumbnailUrl;

    public AlbumUpdateResponse(Album album) {
        this.id = album.getId();
        this.title = album.getTitle();
        this.description = album.getDescription();
        this.thumbnailUrl = album.getThumbnailUrl();
    }
}
