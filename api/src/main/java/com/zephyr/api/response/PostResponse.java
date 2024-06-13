package com.zephyr.api.response;

import com.zephyr.api.domain.Post;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate memoryDate;
    private LocalDateTime createdAt;
    private Long thumbnailMemoryId;
    private String thumbnailUrl;
    private MemberResponse author;
    private SeriesResponse series;
    private List<MemoryResponse> memories;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.memoryDate = post.getMemoryDate();
        this.createdAt = post.getCreatedAt();
        this.thumbnailMemoryId = post.getThumbnailMemory().getId();
        this.thumbnailUrl = post.getThumbnailMemory().getContentUrl();
        this.author = new MemberResponse(post.getAuthor());
        this.series = new SeriesResponse(post.getSeries());
        this.memories = post.getMemories().stream().map(MemoryResponse::new).toList();
    }
}
