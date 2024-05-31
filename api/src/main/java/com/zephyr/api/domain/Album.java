package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Album {

    private Long id;
    private String title;
    private Member owner;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime created;
    private LocalDateTime updated;
    private List<Subscribe> subscribes;

    @Builder
    private Album(String title, Member owner, String description, String thumbnailUrl) {
        this.title = title;
        this.owner = owner;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOwner(Member owner) {
        this.owner = owner;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


}
