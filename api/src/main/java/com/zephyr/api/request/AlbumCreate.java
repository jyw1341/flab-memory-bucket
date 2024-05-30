package com.zephyr.api.request;

import lombok.Data;

@Data
public class AlbumCreate {

    private final String title;
    private final String description;
    private final String thumbnailUrl;

}
