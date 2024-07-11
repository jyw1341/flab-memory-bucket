package com.zephyr.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeriesUpdateRequest {

    private final String seriesName;
}
