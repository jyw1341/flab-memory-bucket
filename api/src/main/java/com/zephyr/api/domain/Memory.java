package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Memory {

    private Long id;
    private final Post post;
    private final String caption;
    private final String contentUrl;
    private final String locationName;
    private final String locationUrl;

    @Builder
    private Memory(Post post, String caption, String contentUrl, String locationName, String locationUrl) {
        this.post = post;
        this.caption = caption;
        this.contentUrl = contentUrl;
        this.locationName = locationName;
        this.locationUrl = locationUrl;
    }
}
