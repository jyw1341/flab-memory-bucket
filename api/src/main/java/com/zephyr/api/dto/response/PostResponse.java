package com.zephyr.api.dto.response;

import com.zephyr.api.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class PostResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate memoryDate;
    private LocalDateTime createdAt;
    private String thumbnailUrl;
    private MemberResponse author;
    private String series;
    private List<MemoryResponse> memories;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.memoryDate = post.getMemoryDate();
        this.createdAt = post.getCreatedAt();
        this.thumbnailUrl = post.getThumbnailUrl();
        this.series = post.getSeries() == null ? null : post.getSeries().getName();
        this.author = new MemberResponse(post.getAuthor());
        this.memories = post.getMemories().stream().map(MemoryResponse::new).toList();
    }
}
