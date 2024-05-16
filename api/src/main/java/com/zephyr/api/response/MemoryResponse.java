package com.zephyr.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class MemoryResponse {

    private final String memoryTitle;
    private final String memoryDescription;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    @Builder
    public MemoryResponse(String memoryTitle, String memoryDescription, LocalDateTime startDate, LocalDateTime endDate) {
        this.memoryTitle = memoryTitle;
        this.memoryDescription = memoryDescription;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
