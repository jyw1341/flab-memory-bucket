package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SeriesPostDto {
    private Long seriesId;
    private String seriesName;
    private Integer postCount;
    private LocalDate firstDate;
    private LocalDate lastDate;
    private String thumbnail;
}
