package com.zephyr.api.service;

import com.zephyr.api.domain.Series;
import com.zephyr.api.exception.SeriesNotFoundException;
import com.zephyr.api.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final MessageSource messageSource;

    public Series get(Long seriesId) {
        return seriesRepository.findById(seriesId)
                .orElseThrow(() -> new SeriesNotFoundException(messageSource));
    }
}
