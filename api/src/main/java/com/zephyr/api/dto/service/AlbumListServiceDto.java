package com.zephyr.api.dto.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumListServiceDto {

    private final Long memberId;
    private final Integer page;
    private final Integer size;
}
