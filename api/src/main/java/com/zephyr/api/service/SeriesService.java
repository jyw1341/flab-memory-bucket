package com.zephyr.api.service;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Series;
import com.zephyr.api.dto.SeriesAggregationDto;
import com.zephyr.api.dto.SeriesCreateServiceDto;
import com.zephyr.api.dto.SeriesUpdateServiceDto;
import com.zephyr.api.exception.SeriesNotFoundException;
import com.zephyr.api.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final AlbumService albumService;
    private final MessageSource messageSource;

    public Series create(SeriesCreateServiceDto dto) {
        Album album = albumService.get(dto.getAlbumId());

        Series series = Series.builder()
                .album(album)
                .postCount(0L)
                .name(dto.getSeriesName())
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

    public List<Series> getList(Long albumId) {
        return seriesRepository.findByAlbumId(albumId);
    }

    @Transactional
    public void updateName(SeriesUpdateServiceDto dto) {
        Series series = seriesRepository.findById(dto.getSeriesId())
                .orElseThrow(() -> new SeriesNotFoundException(messageSource));

        series.setName(dto.getSeriesName());
    }

    @Transactional
    public void updateAggregation(Long seriesId) {
        Series series = get(seriesId);
        Optional<SeriesAggregationDto> optional = seriesRepository.findSeriesAggregationDto(seriesId);

        if (optional.isPresent()) {
            SeriesAggregationDto seriesAggregationDto = optional.get();
            String thumbnail = seriesRepository.findSeriesThumbnail(seriesId).orElseThrow();

            series.setPostCount(seriesAggregationDto.getPostCount());
            series.setFirstDate(seriesAggregationDto.getFirstDate());
            series.setLastDate(seriesAggregationDto.getLastDate());
            series.setThumbnailUrl(thumbnail);
            return;
        }

        series.setPostCount(0L);
        series.setFirstDate(null);
        series.setLastDate(null);
        series.setThumbnailUrl(null);
    }

    public void delete(Long seriesId) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new SeriesNotFoundException(messageSource));

        seriesRepository.delete(series);
    }
}
