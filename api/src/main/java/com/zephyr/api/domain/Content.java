package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Content {

    private final Integer index;
    private final Memory memory;
    private final String title;
    private final String description;
    private final String url;
    private Long id;

    @Builder
    private Content(Memory memory, Integer index, String title, String description, String url) {
        this.memory = memory;
        this.index = index;
        this.title = title;
        this.description = description;
        this.url = url;
    }
}
