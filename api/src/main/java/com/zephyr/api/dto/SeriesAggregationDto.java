package com.zephyr.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SeriesAggregationDto {

    private Long id;
    private String name;
    private LocalDate firstMemoryDate;
    private LocalDate lastMemoryDate;
    private String thumbnailUrl;
}
