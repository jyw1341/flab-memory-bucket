package com.zephyr.api.repository;

import com.zephyr.api.dto.SeriesPostDto;

import java.util.List;

public interface SeriesCustomRepository {

    List<SeriesPostDto> findSeriesAggregationDto(Long albumId);

}
