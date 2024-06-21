package com.zephyr.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumListRequest {

    private Integer page;
    private Integer size;
}
