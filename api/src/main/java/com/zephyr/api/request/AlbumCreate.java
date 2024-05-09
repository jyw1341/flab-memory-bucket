package com.zephyr.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AlbumCreate {

    private final String ownerId;
    private final String albumTitle;
    private final String albumDescription;

    @Builder
    public AlbumCreate(String ownerId, String albumTitle, String albumDescription) {
        this.ownerId = ownerId;
        this.albumTitle = albumTitle;
        this.albumDescription = albumDescription;
    }
}
