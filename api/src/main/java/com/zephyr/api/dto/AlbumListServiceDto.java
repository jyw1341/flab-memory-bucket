package com.zephyr.api.dto;

import lombok.Getter;

@Getter
public class AlbumListServiceDto {

    private final Long memberId;
    private final Integer page;
    private final Integer size;

    public AlbumListServiceDto(Long memberId, Integer page, Integer size) {
        this.memberId = memberId;
        this.page = (page != null) ? page : 1;
        this.size = (size != null) ? size : 10;
    }
}
