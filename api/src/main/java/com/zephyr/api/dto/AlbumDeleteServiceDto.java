package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumDeleteServiceDto {

    private final Long albumId;
    private final Long memberId;
}
