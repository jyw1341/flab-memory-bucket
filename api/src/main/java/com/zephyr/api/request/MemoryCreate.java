package com.zephyr.api.request;

import lombok.Data;

@Data
public class MemoryCreate {

    private final String caption;
    private final String contentUrl;
    private final String locationName;
    private final String locationUrl;
}
