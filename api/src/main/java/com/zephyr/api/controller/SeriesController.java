package com.zephyr.api.controller;

import com.zephyr.api.domain.Series;
import com.zephyr.api.dto.SeriesAggregationDto;
import com.zephyr.api.dto.SeriesCreateServiceDto;
import com.zephyr.api.dto.SeriesUpdateServiceDto;
import com.zephyr.api.dto.mapper.SeriesCreateMapper;
import com.zephyr.api.dto.mapper.SeriesUpdateMapper;
import com.zephyr.api.dto.request.SeriesCreateRequest;
import com.zephyr.api.dto.request.SeriesUpdateRequest;
import com.zephyr.api.dto.response.SeriesResponse;
import com.zephyr.api.service.SeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    @PostMapping("/albums/{albumId}/series")
    public ResponseEntity<Void> create(@PathVariable Long albumId, @RequestBody SeriesCreateRequest request) {
        SeriesCreateServiceDto serviceDto = SeriesCreateMapper.INSTANCE.toSeriesCreateServiceDto(albumId, request);
        Series series = seriesService.create(serviceDto);
        String path = String.format("/series/%d", series.getId());

        return ResponseEntity.created(URI.create(path)).build();
    }

    @GetMapping("/albums/{albumId}/series")
    public List<SeriesResponse> getList(@PathVariable Long albumId) {
        return seriesService.getList(albumId).stream().map(SeriesResponse::new).toList();
    }

    @GetMapping("/albums/{albumId}/series-aggregations")
    public List<SeriesAggregationDto> getDtoList(@PathVariable Long albumId) {
        return seriesService.getSeriesAggregations(albumId);
    }

    @GetMapping("/series/{seriesId}")
    public SeriesResponse get(@PathVariable Long seriesId) {
        Series series = seriesService.get(seriesId);

        return new SeriesResponse(series);
    }

    @PatchMapping("/series/{seriesId}")
    public void update(@PathVariable Long seriesId, @RequestBody SeriesUpdateRequest request) {
        Long memberId = 1L;
        SeriesUpdateServiceDto serviceDto = SeriesUpdateMapper.INSTANCE.toSeriesUpdateServiceDto(memberId, seriesId, request);
        seriesService.updateName(serviceDto);
    }

    @DeleteMapping("/series/{seriesId}")
    public void delete(@PathVariable Long seriesId) {
        Long memberId = 1L;
        seriesService.delete(seriesId);
    }
}
