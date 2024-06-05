package com.zephyr.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PostCreate {

    @NotNull
    private final Long albumId;

    @NotNull
    private final Long seriesId;

    @NotBlank
    private final String title;

    private final String description;

    @NotNull
    private final LocalDate memoryDate;

    @NotNull
    private final Integer thumbnailIndex;

    @NotEmpty
    private final List<MemoryCreate> memoryCreates;
}
