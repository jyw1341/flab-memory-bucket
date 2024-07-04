package com.zephyr.api.repository;

import com.zephyr.api.dto.SeriesAggregationDto;

import java.util.Optional;

public interface SeriesCustomRepository {

    Optional<SeriesAggregationDto> findSeriesAggregationDto(Long seriesId);

    Optional<String> findSeriesThumbnail(Long seriesId);
}
