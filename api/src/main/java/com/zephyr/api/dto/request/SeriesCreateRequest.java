package com.zephyr.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeriesCreateRequest {

    private Long albumId;
    private String name;

}
