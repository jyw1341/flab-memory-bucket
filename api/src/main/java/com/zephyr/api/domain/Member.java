package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class Member {

    private Long id;
    private String name;
    private String email;
    private String profileImageUrl;
    private LocalDateTime registered;
    @Builder.Default
    private List<Album> albums = new ArrayList<>();
    @Builder.Default
    private List<Subscribe> subscribes = new ArrayList<>();


}
