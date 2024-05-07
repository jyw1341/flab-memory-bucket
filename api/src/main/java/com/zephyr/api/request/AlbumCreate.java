package com.zephyr.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AlbumCreate {

    private final String ownerId;
    private final String albumName;
    private final String albumDescription;

    @Builder
    public AlbumCreate(String ownerId, String albumName, String albumDescription) {
        this.ownerId = ownerId;
        this.albumName = albumName;
        this.albumDescription = albumDescription;
    }
}
