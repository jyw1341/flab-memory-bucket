package com.zephyr.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlbumCreate {

    @NotBlank(message = "notBlank.album.title")
    @Size(max = 30)
    private final String title;

    @Size(max = 200)
    private final String description;

    private final String thumbnailUrl;

}
