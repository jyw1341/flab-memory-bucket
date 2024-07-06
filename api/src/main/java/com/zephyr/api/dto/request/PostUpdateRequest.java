package com.zephyr.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PostUpdateRequest {

    private final Long seriesId;
    private final Long coverMemoryId;
    private final String title;
    private final String description;
    private final LocalDate memoryDate;

}
