package com.zephyr.api.dto.request;

import lombok.Data;

@Data
public class PostSearchRequest {

    private final String author;
    private final String title;
    private final Long seriesId;

    public PostSearchRequest(String author, String title, Long seriesId) {
        this.author = author;
        this.title = title;
        this.seriesId = seriesId;
    }
}
