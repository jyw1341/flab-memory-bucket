package com.zephyr.api.domain;

import com.zephyr.api.enums.AlbumAuthority;
import com.zephyr.api.enums.SubscribeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Subscribe {

    private Long id;
    private Album album;
    private Member subscriber;
    private AlbumAuthority authority;
    private SubscribeStatus status;
    private LocalDateTime subscribed;

    @Builder
    private Subscribe(Album album, Member subscriber, AlbumAuthority authority, SubscribeStatus status) {
        this.album = album;
        this.subscriber = subscriber;
        this.authority = authority;
        this.status = status;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setSubscriber(Member subscriber) {
        this.subscriber = subscriber;
    }

    public void setAuthority(AlbumAuthority authority) {
        this.authority = authority;
    }

    public void setStatus(SubscribeStatus status) {
        this.status = status;
    }
}
