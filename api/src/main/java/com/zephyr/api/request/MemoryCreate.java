package com.zephyr.api.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MemoryCreate {

    private final Long albumId;
    private final String title;
    private final String description;
    private final LocalDateTime memoryDate;
    private final List<String> tags;
    private final List<ContentCreate> contents;

}
