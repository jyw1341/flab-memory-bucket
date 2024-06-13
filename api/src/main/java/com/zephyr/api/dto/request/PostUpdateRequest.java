package com.zephyr.api.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PostUpdateRequest {

    private final String series;

    private final String title;

    private final String description;

    private final LocalDate memoryDate;

    private final String thumbnailUrl;

    private final List<MemoryUpdateRequest> memoryUpdateRequests;
}
