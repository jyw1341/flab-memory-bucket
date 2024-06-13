package com.zephyr.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostCreateRequest {

    @NotNull
    private final Long albumId;

    private final String series;

    @NotBlank
    private final String title;

    private final String description;

    private final LocalDate memoryDate;

    private final String thumbnailUrl;

    @NotEmpty
    private final List<MemoryCreateRequest> memoryCreateRequests;
}
