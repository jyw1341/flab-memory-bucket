package com.zephyr.api.controller;

import com.zephyr.api.request.MemoryUpdate;
import com.zephyr.api.response.MemoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/memories")
public class MemoryController {

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
