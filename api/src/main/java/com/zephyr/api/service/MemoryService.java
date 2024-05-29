package com.zephyr.api.service;

import com.zephyr.api.domain.Memory;
import com.zephyr.api.repository.MemoryRepository;
import com.zephyr.api.request.MemoryCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final MemoryRepository memoryRepository;

    public Memory create(MemoryCreate memoryCreate) {

        return Memory.builder().build();
    }
}
