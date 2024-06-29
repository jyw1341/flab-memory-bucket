package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberUpdateServiceDto {

    private final Long memberId;
    private final String username;
    private final String profileUrl;
}
