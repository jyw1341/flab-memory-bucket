package com.zephyr.api.response;

import com.zephyr.api.domain.Member;
import lombok.Data;

@Data
public class MemberResponse {

    private final Long id;
    private final String name;
    private final String profileImageUrl;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.profileImageUrl = member.getProfileImageUrl();
    }
}
