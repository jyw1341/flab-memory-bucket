package com.zephyr.api.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemoryResponse {

    private final String title;
    private final String description;
    private final LocalDateTime memoryDate;
    

}
