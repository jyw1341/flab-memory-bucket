package com.zephyr.api.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContentCreate {

    public static final int MAX_CONTENT_TITLE_SIZE = 15;

    @Size(max = MAX_CONTENT_TITLE_SIZE)
    private final String title;
    private final String description;
    private final String url;
    private final Integer index;
}
