package com.zephyr.api.repository;

import com.zephyr.api.domain.Series;

import java.util.List;

public interface SeriesCustomRepository {

    List<Series> findSeriesPost(Long albumId);

}
