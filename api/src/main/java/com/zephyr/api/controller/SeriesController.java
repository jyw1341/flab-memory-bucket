package com.zephyr.api.controller;

import com.zephyr.api.domain.Series;
import com.zephyr.api.dto.request.SeriesCreateRequest;
import com.zephyr.api.dto.response.SeriesResponse;
import com.zephyr.api.service.SeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/series")
public class SeriesController {

    private final SeriesService seriesService;

    @PostMapping
    public ResponseEntity<SeriesResponse> create(@RequestBody SeriesCreateRequest request) {
        Series series = seriesService.create(request);

        return ResponseEntity.created(URI.create("/series/" + series.getId())).body(new SeriesResponse(series));
    }
}
