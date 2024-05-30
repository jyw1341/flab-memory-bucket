package com.zephyr.api.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlbumUpdate {
    private final String title;
    private final String description;
    private final String thumbnailUrl;
}
