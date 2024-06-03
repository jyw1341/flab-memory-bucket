package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Comment {

    private Long id;
    private Member member;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
