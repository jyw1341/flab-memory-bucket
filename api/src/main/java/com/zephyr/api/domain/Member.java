package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Member {

    private Long id;
    private String name;
    private String email;
    private String profileImageUrl;
    private LocalDateTime registered;

}
