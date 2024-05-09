package com.zephyr.api.request;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class MemoryCreate {

    private final String title;
    private final String description;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public MemoryCreate(String title, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
