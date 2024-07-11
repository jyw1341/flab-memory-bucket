package com.zephyr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberCreateServiceDto {

    private final String email;
    private final String username;
    private final String profileUrl;
}
