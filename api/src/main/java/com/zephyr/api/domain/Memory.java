package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Memory {

    private Long id;
    private Long albumId;
    private final Member author;
    private final String title;
    private final String description;
    private final List<Tag> tags;
    private final List<Content> contents;
    private final List<Comment> comments;

    @Builder
    private Memory(Member author, String title, String description, List<Tag> tags, List<Content> contents, List<Comment> comments) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.contents = contents;
        this.comments = comments;
    }
}
