package com.zephyr.api.dto.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumDeleteServiceDto {

    private final Long albumId;
    private final Long memberId;
}
