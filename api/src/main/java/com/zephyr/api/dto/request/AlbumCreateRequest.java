package com.zephyr.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlbumCreateRequest {

    @NotBlank
    @Size(max = 30)
    private final String title;

    @Size(max = 200)
    private final String description;

    private final String thumbnailUrl;

}
