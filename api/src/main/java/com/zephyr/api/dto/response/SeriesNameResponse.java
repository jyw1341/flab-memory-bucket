package com.zephyr.api.dto.response;

import com.zephyr.api.domain.Series;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeriesNameResponse {

    private Long id;
    private String name;

    public SeriesNameResponse(Series series) {
        this.id = series.getId();
        this.name = series.getName();
    }
}
