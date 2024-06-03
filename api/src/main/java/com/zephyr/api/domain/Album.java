package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class Album {

    private Long id;
    private String title;
    private Member owner;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime created;
    private LocalDateTime updated;
    private List<Subscribe> subscribes;

}
