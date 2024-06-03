package com.zephyr.api.response;

import com.zephyr.api.domain.Subscribe;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscribeResponse {

    private final Long memberId;
    private final String name;
    private final String profileImageUrl;
    private final LocalDateTime subscribed;

    public SubscribeResponse(Subscribe subscribe) {
        this.memberId = subscribe.getSubscriber().getId();
        this.name = subscribe.getSubscriber().getName();
        this.profileImageUrl = subscribe.getSubscriber().getProfileImageUrl();
        this.subscribed = subscribe.getSubscribed();
    }
}
