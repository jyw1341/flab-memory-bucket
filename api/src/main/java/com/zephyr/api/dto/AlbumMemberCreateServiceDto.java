package com.zephyr.api.dto;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Member;
import com.zephyr.api.enums.AlbumMemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumMemberCreateServiceDto {

    private final Member member;
    private final Album album;
    private final AlbumMemberRole role;
}
