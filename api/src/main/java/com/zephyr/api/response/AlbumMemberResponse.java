package com.zephyr.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class AlbumMemberResponse {
    private final Long memberId;
    private final String memberName;
    private final String memberProfileImage;
    private final LocalDateTime registerDate;

    @Builder
    public AlbumMemberResponse(Long memberId, String memberName, String memberProfileImage, LocalDateTime registerDate) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberProfileImage = memberProfileImage;
        this.registerDate = registerDate;
    }
}
