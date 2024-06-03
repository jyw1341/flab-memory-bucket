package com.zephyr.api.domain;

import com.zephyr.api.enums.AlbumAuthority;
import com.zephyr.api.enums.SubscribeStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Subscribe {

    private Long id;
    private Album album;
    private Member subscriber;
    private AlbumAuthority authority;
    private SubscribeStatus status;
    private LocalDateTime subscribed;
}
