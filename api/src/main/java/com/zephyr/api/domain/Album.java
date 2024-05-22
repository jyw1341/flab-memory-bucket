package com.zephyr.api.domain;

import com.zephyr.api.request.AlbumUpdate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class Album {

    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime created;
    private LocalDateTime updated;
    private List<AlbumMember> subscribes;

    public void update(AlbumUpdate update) {
        this.title = update.getTitle();
        this.description = update.getDescription();
        this.thumbnailUrl = update.getThumbnailUrl();
    }

}
