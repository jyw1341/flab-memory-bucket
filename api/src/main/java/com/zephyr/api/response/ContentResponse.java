package com.zephyr.api.response;

import com.zephyr.api.domain.Content;
import lombok.Data;

@Data
public class ContentResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String url;
    private final Integer index;

    public ContentResponse(Content content) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.description = content.getDescription();
        this.url = content.getUrl();
        this.index = content.getIndex();
    }
}
