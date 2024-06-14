package com.zephyr.api.dto;

import com.zephyr.api.dto.request.MemoryCreateRequest;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PostCreateServiceDto {

    private final String memberId;
    private final Long albumId;
    private final Long seriesId;
    private final String title;
    private final String description;
    private final LocalDate memoryDate;
    private final String thumbnailUrl;
    private final List<MemoryCreateRequest> memoryCreateRequests;
}
