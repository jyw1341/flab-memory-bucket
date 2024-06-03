package com.zephyr.api.response;

import com.zephyr.api.domain.Album;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumListResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String thumbnailUrl;
    private final LocalDateTime created;
    private final MemberResponse owner;
    private final Integer albumMemberCount;

    public AlbumListResponse(Album album) {
        this.id = album.getId();
        this.title = album.getTitle();
        this.description = album.getDescription();
        this.thumbnailUrl = album.getThumbnailUrl();
        this.created = album.getCreated();
        this.owner = new MemberResponse(album.getOwner());
        this.albumMemberCount = album.getSubscribes().size();
    }
}
