package com.zephyr.api.response;

import com.zephyr.api.domain.Memory;
import lombok.Data;

@Data
public class MemoryResponse {

    private Long id;
    private String contentUrl;
    private String caption;
    private Integer commentCount;

    public MemoryResponse(Memory memory) {
        this.id = memory.getId();
        this.contentUrl = memory.getContentUrl();
        this.caption = memory.getCaption();
        this.commentCount = memory.getComments().size();
    }
}
