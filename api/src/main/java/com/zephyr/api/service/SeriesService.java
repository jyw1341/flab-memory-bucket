package com.zephyr.api.service;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Series;
import com.zephyr.api.dto.request.SeriesCreateRequest;
import com.zephyr.api.exception.SeriesNotFoundException;
import com.zephyr.api.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final AlbumService albumService;
    private final MessageSource messageSource;

    public Series create(SeriesCreateRequest dto) {
        Album album = albumService.get(dto.getAlbumId());

        Series series = Series.builder()
                .album(album)
                .name(dto.getName())
                .build();
        seriesRepository.save(series);

        return series;
    }

    public Series get(Long seriesId) {
        if (seriesId == null) {
            return null;
        }

        return seriesRepository.findById(seriesId)
                .orElseThrow(() -> new SeriesNotFoundException(messageSource));
    }
}
