package com.zephyr.api.dto.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostUpdateServiceDto {

    private final Long memberId;

    private final Long postId;

    private final Long seriesId;

    private final String title;

    private final String description;

    private final LocalDate memoryDate;

    private final String thumbnailUrl;

    private final List<MemoryUpdateServiceDto> memoryUpdateServiceDtos;
}
