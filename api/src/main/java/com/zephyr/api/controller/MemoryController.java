package com.zephyr.api.controller;

import com.zephyr.api.request.MemoryCreate;
import com.zephyr.api.request.MemoryUpdate;
import com.zephyr.api.response.MemoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

@Slf4j
@RestController
@RequestMapping("/memories")
public class MemoryController {

    @GetMapping
    public List<MemoryResponse> getMemories(@PathVariable Long albumId) {
        List<MemoryResponse> response = LongStream.range(1, 11).mapToObj(value -> MemoryResponse.builder()
                .memoryTitle("title" + value)
                .memoryDescription("description" + value)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build()
        ).toList();

        return response;
    }

    @PostMapping
    public ResponseEntity<MemoryResponse> createMemory(@PathVariable Long albumId, @RequestBody MemoryCreate request) {
        MemoryResponse response = MemoryResponse.builder()
                .memoryTitle("title")
                .memoryDescription("description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        return ResponseEntity.created(URI.create("/memories/1")).body(response);
    }

    @GetMapping("/{memoryId}")
    public MemoryResponse get(@PathVariable Long memoryId) {
        return null;
    }

    @PatchMapping("/{memoryId}")
    public MemoryResponse update(@PathVariable Long memoryId, @RequestBody MemoryUpdate memoryUpdate) {
        return null;
    }

    @DeleteMapping("/{memoryId}")
    public void delete(@PathVariable Long memoryId) {

    }
}
