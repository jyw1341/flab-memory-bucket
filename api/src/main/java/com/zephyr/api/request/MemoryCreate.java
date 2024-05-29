package com.zephyr.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MemoryCreate {

    public static final int MEMORY_TITLE_MAX = 30;
    public static final int MEMORY_DESCRIPTION_MAX = 100;

    @NotNull
    private final Long albumId;

    @NotBlank(message = "{notBlank.memory.title}")
    @Size(max = MEMORY_TITLE_MAX)
    private final String title;

    @Size(max = MEMORY_DESCRIPTION_MAX)
    private final String description;

    @NotNull
    private final LocalDateTime memoryDate;

    private final List<String> tags;

    @NotEmpty
    private final List<ContentCreate> contents;

}
