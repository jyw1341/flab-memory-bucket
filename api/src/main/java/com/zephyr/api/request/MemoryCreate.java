package com.zephyr.api.request;

import lombok.Data;

@Data
public class MemoryCreate {

    private final Integer index;
    private final String contentUrl;
    private final String caption;
}
