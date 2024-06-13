package com.zephyr.api.dto.response;

import com.zephyr.api.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class MemberResponse {

    private final Long id;
    private final String name;
    private final String profileImageUrl;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getUsername();
        this.profileImageUrl = member.getProfileImageUrl();
    }
}
