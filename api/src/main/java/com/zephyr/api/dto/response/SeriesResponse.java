package com.zephyr.api.dto.response;

import com.zephyr.api.domain.Series;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@ToString
public class SeriesResponse {

    private Long id;
    private String name;
    private Long postCount;
    private LocalDate firstDate;
    private LocalDate lastDate;
    private String thumbnailUrl;

    public SeriesResponse(Series series) {
        this.id = series.getId();
        this.name = series.getName();
        this.postCount = series.getPostCount();
        this.firstDate = series.getFirstDate();
        this.lastDate = series.getLastDate();
        this.thumbnailUrl = series.getThumbnailUrl();
    }
}
