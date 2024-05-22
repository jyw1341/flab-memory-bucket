package com.zephyr.api.response;

import com.zephyr.api.domain.Album;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlbumResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String thumbnailUrl;
    private final LocalDateTime created;
    private final List<AlbumMemberResponse> subscribes;
    private final Long memoryCount;

    public AlbumResponse(Album album, List<AlbumMemberResponse> subscribes, Long memoryCount) {
        this.id = album.getId();
        this.title = album.getTitle();
        this.description = album.getDescription();
        this.thumbnailUrl = album.getThumbnailUrl();
        this.created = album.getCreated();
        this.subscribes = subscribes;
        this.memoryCount = memoryCount;
    }
}
