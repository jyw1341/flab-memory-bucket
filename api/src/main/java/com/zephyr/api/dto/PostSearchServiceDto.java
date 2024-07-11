package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Pageable;

@Data
@AllArgsConstructor
public class PostSearchServiceDto {

    private final Long albumId;
    private final Long seriesId;
    private final String author;
    private final String title;
    private final Pageable pageable;
}
