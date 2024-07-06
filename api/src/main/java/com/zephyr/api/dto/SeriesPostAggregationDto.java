package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class SeriesPostAggregationDto {

    private final Long id;
    private final String name;
    private final LocalDate firstDate;
    private final LocalDate lastDate;
    private final String thumbnailUrl;
}
