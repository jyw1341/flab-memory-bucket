package com.zephyr.api.repository;

import com.zephyr.api.domain.Memory;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryRepository {

    public Memory save(Memory memory) {
        return memory;
    }
}
