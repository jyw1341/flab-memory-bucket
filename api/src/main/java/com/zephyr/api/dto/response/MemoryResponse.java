package com.zephyr.api.dto.response;

import com.zephyr.api.domain.Memory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class MemoryResponse {

    private Long id;
    private String contentUrl;
    private String caption;
    private Double index;
//    private Integer commentCount;

    public MemoryResponse(Memory memory) {
        this.id = memory.getId();
        this.contentUrl = memory.getContentUrl();
        this.caption = memory.getCaption();
        this.index = memory.getIndex();
//        this.commentCount = memory.getComments().size();
    }
}
