package com.zephyr.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AlbumMemberRequest {

    private final String email;

    @Builder
    public AlbumMemberRequest(String email) {
        this.email = email;
    }
}
