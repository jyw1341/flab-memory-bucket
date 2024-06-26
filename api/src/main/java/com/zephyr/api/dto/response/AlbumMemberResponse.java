package com.zephyr.api.dto.response;

import com.zephyr.api.domain.AlbumMember;
import lombok.Data;

@Data
public class AlbumMemberResponse {

    private final Long memberId;
    private final String name;
    private final String profileImageUrl;

    public AlbumMemberResponse(AlbumMember albumMember) {
        this.memberId = albumMember.getMember().getId();
        this.name = albumMember.getMember().getUsername();
        this.profileImageUrl = albumMember.getMember().getProfileUrl();
    }
}
