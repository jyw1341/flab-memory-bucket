package com.zephyr.api.repository;

import com.zephyr.api.domain.Memory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemoryRepository {

    public Memory save(Memory memory) {
        return memory;
    }

    public Optional<Memory> findByAlbumIdAndMemoryId(Long albumId, Long memoryId) {
        return Optional.empty();
    }
}
