package com.zephyr.api.dto.response;

import com.zephyr.api.domain.AlbumMember;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumMemberResponse {

    private final Long memberId;
    private final String name;
    private final String profileImageUrl;
    private final LocalDateTime subscribed;

    public AlbumMemberResponse(AlbumMember albumMember) {
        this.memberId = albumMember.getMember().getId();
        this.name = albumMember.getMember().getUsername();
        this.profileImageUrl = albumMember.getMember().getProfileImageUrl();
        this.subscribed = albumMember.getCreatedAt();
    }
}
