package com.zephyr.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberCreateRequest {

    private final String email;
    private final String username;
    private final String profileUrl;

}
