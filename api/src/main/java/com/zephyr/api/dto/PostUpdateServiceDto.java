package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PostUpdateServiceDto {

    private final Long memberId;
    private final Long postId;
    private final Long seriesId;
    private final Long coverMemoryId;
    private final String title;
    private final String description;
    private final LocalDate memoryDate;
}
