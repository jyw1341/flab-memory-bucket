package com.zephyr.api.domain;

import com.zephyr.api.enums.AlbumAuthority;
import com.zephyr.api.enums.SubscribeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AlbumMember {

    private final Long id;
    private final Member member;
    private final Album album;
    private final AlbumAuthority authority;
    private final SubscribeStatus status;
    private final LocalDateTime subscribed;
}
