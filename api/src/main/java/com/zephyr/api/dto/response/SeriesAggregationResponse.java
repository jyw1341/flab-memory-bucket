package com.zephyr.api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SeriesAggregationResponse {

    private Long id;
    private String name;
    private LocalDate firstMemoryDate;
    private LocalDate lastMemoryDate;
    private String thumbnailUrl;
}
