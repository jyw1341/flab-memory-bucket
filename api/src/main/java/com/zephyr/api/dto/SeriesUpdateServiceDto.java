package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeriesUpdateServiceDto {
    private final Long memberId;
    private final Long seriesId;
    private final String seriesName;
}
