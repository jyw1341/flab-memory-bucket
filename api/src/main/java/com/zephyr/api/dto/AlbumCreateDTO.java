package com.zephyr.api.dto;

import lombok.Data;

@Data
public class AlbumCreateDTO {

    private final String memberEmail;
    private final String title;
    private final String description;
    private final String thumbnailUrl;

}
