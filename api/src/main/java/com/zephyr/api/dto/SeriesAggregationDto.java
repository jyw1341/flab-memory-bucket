package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SeriesAggregationDto {

    private final Long seriesId;
    private final Long postCount;
    private final LocalDate firstDate;
    private final LocalDate lastDate;
}
