package com.zephyr.api.response;

import com.zephyr.api.domain.AlbumMember;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumMemberResponse {

    private final Long memberId;
    private final String name;
    private final String profileImageUrl;
    private final LocalDateTime subscribed;

    public AlbumMemberResponse(AlbumMember subscribe) {
        this.memberId = subscribe.getMember().getId();
        this.name = subscribe.getMember().getName();
        this.profileImageUrl = subscribe.getMember().getProfileImageUrl();
        this.subscribed = subscribe.getSubscribed();
    }
}
