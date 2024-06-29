package com.zephyr.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlCreateResponse {

    private final Integer fileIndex;
    private final String url;

}
