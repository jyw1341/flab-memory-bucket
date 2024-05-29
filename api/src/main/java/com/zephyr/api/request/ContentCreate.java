package com.zephyr.api.request;

import lombok.Data;

@Data
public class ContentCreate {

    private final String title;
    private final String description;
    private final String url;
    private final Integer index;
}
