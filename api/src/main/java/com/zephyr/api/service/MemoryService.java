package com.zephyr.api.service;

import com.zephyr.api.domain.Memory;
import com.zephyr.api.dto.service.MemoryUpdateServiceDto;
import com.zephyr.api.exception.MemoryNotFoundException;
import com.zephyr.api.repository.MemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final MemoryRepository memoryRepository;
    private final MessageSource messageSource;

    @Transactional
    public void update(MemoryUpdateServiceDto dto) {
        Memory memory = memoryRepository.findById(dto.getId())
                .orElseThrow(() -> new MemoryNotFoundException(messageSource));

        memory.setIndex(dto.getIndex());
        memory.setCaption(dto.getCaption());
    }
}
