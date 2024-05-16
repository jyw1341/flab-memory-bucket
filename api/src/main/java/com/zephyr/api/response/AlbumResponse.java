package com.zephyr.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class AlbumResponse {

    private final Long albumId;
    private final String albumTitle;
    private final String albumDescription;
    private final String albumCover;
    private final LocalDateTime createdAt;

    @Builder
    public AlbumResponse(Long albumId, String albumTitle, String albumDescription, String albumCover, LocalDateTime createdAt) {
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.albumDescription = albumDescription;
        this.albumCover = albumCover;
        this.createdAt = createdAt;
    }
}
