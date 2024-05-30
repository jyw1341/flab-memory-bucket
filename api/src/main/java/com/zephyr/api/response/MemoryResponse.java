package com.zephyr.api.response;

import com.zephyr.api.domain.Memory;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemoryResponse {

    private final Long memoryId;
    private final MemberResponse author;
    private final String title;
    private final String description;
    private final String memoryDate;
    private final List<String> tags;
    private final List<ContentResponse> contents;
    private final List<CommentResponse> comments;

    public MemoryResponse(Memory memory) {
        this.memoryId = memory.getId();
        this.author = new MemberResponse(memory.getAuthor());
        this.title = memory.getTitle();
        this.description = memory.getDescription();
        this.memoryDate = memory.getMemoryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.tags = new ArrayList<>();
        this.contents = memory.getContents().stream().map(ContentResponse::new).toList();
        this.comments = memory.getComments().stream().map(CommentResponse::new).toList();
    }
}
