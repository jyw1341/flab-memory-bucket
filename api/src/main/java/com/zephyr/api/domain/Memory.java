package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Memory {

    private Long id;
    private final Album album;
    private final Member author;
    private final String title;
    private final String description;
    private final LocalDateTime memoryDate;
    private List<Tag> tags;
    private List<Content> contents;
    private List<Comment> comments;

    @Builder
    private Memory(Album album, Member author, String title, String description, LocalDateTime memoryDate) {
        this.album = album;
        this.author = author;
        this.title = title;
        this.description = description;
        this.memoryDate = memoryDate;
    }
}
