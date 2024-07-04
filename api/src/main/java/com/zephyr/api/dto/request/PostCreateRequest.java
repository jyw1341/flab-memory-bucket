package com.zephyr.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostCreateRequest {

    private final Long seriesId;
    private final String title;
    private final String description;
    private final LocalDate memoryDate;
    private final String thumbnailUrl;
    private final List<MemoryCreateRequest> memoryCreateRequests;
}
