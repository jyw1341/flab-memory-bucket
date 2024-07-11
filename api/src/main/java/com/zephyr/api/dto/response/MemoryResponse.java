package com.zephyr.api.dto.response;

import com.zephyr.api.domain.Memory;
import com.zephyr.api.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemoryResponse {

    private Long id;
    private ContentType contentType;
    private String contentUrl;
    private String caption;
    private Double index;

    public MemoryResponse(Memory memory) {
        this.id = memory.getId();
        this.contentType = memory.getContentType();
        this.contentUrl = memory.getContentUrl();
        this.caption = memory.getCaption();
        this.index = memory.getIndex();
    }
}
