package com.zephyr.api.service;

import com.zephyr.api.domain.Memory;
import com.zephyr.api.domain.Post;
import com.zephyr.api.dto.request.MemoryCreateRequest;
import com.zephyr.api.dto.request.MemoryUpdateRequest;
import com.zephyr.api.exception.MemoryNotFoundException;
import com.zephyr.api.exception.PostNotFoundException;
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
    public Memory create(Post post, MemoryCreateRequest dto) {
        Memory memory = Memory.builder()
                .post(post)
                .index(dto.getIndex())
                .contentUrl(dto.getContentUrl())
                .caption(dto.getCaption())
                .build();

        memoryRepository.save(memory);

        return memory;
    }

    @Transactional
    public void update(MemoryUpdateRequest dto) {
        Memory memory = memoryRepository.findById(dto.getId())
                .orElseThrow(() -> new MemoryNotFoundException(messageSource));

        memory.setIndex(dto.getIndex());
        memory.setCaption(dto.getCaption());
    }

    public Memory get(Long postId) {
        return memoryRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(messageSource));
    }
}
