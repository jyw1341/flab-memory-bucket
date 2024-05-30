package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class Memory {

    private final Album album;
    private final Member author;
    private final String title;
    private final String description;
    private final LocalDateTime memoryDate;
    private Long id;
    private List<Content> contents;
    private List<Comment> comments;
    private List<Tag> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
