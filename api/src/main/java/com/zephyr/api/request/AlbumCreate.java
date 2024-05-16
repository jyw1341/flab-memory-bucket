package com.zephyr.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@ToString
public class AlbumCreate {

    private final String ownerId;
    private final String albumTitle;
    private final String albumDescription;
    private final MultipartFile albumCover;

    @Builder
    public AlbumCreate(String ownerId, String albumTitle, String albumDescription, MultipartFile albumCover) {
        this.ownerId = ownerId;
        this.albumTitle = albumTitle;
        this.albumDescription = albumDescription;
        this.albumCover = albumCover;
    }
}
