package com.zephyr.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class AlbumResponse {

    private final Long albumId;
    private final String albumName;
    private final String albumDescription;
    private final String albumCover;
    private final LocalDateTime createdAt;

    @Builder
    public AlbumResponse(Long albumId, String albumName, String albumDescription, String albumCover, LocalDateTime createdAt) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.albumDescription = albumDescription;
        this.albumCover = albumCover;
        this.createdAt = createdAt;
    }
}
