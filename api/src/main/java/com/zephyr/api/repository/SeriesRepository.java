package com.zephyr.api.repository;

import com.zephyr.api.domain.Series;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SeriesRepository {

    public Optional<Series> findById(Long id) {
        return Optional.empty();
    }
}
