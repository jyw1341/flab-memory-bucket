package com.zephyr.api.response;

import com.zephyr.api.domain.Series;
import lombok.Data;

@Data
public class SeriesResponse {

    private Long id;
    private String name;

    public SeriesResponse(Series series) {
        this.id = series.getId();
        this.name = series.getName();
    }
}
