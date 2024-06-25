package com.zephyr.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlCreateRequest {

    private final Integer fileIndex;
    private final String fileName;
    private final Long fileSize;
}
