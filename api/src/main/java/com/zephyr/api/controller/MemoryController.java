package com.zephyr.api.controller;

import com.zephyr.api.domain.Memory;
import com.zephyr.api.request.MemoryCreate;
import com.zephyr.api.request.MemoryUpdate;
import com.zephyr.api.response.MemoryResponse;
import com.zephyr.api.service.MemoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/memories")
public class MemoryController {

    private final MemoryService memoryService;

    @PostMapping
    public ResponseEntity<Void> createMemory(@RequestBody @Valid MemoryCreate request) {
        Memory memory = memoryService.create(request, 1L);

        return ResponseEntity.created(URI.create("/memories/" + memory.getId())).build();
    }

    @GetMapping
    public List<MemoryResponse> getMemories() {

        return null;
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
