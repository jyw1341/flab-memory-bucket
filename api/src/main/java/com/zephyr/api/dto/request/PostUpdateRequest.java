package com.zephyr.api.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PostUpdateRequest {

    private final Long seriesId;
    private final String title;
    private final String description;
    private final LocalDate memoryDate;
    private final String thumbnailUrl;

    public PostUpdateRequest(Long seriesId, String title, String description, LocalDate memoryDate, String thumbnailUrl) {
        this.seriesId = seriesId;
        this.title = title;
        this.description = description;
        this.memoryDate = memoryDate;
        this.thumbnailUrl = thumbnailUrl;
    }
}
