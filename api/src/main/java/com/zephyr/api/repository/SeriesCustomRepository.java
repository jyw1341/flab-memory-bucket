package com.zephyr.api.repository;

import com.zephyr.api.dto.SeriesAggregationDto;

import java.util.List;

public interface SeriesCustomRepository {

    List<SeriesAggregationDto> findSeriesAggregationDto(Long albumId);

}
