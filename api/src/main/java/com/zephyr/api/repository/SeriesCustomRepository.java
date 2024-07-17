package com.zephyr.api.repository;

import com.zephyr.api.dto.response.SeriesAggregationResponse;

import java.util.List;

public interface SeriesCustomRepository {

    List<SeriesAggregationResponse> findSeriesAggregationDto(Long albumId);

}
