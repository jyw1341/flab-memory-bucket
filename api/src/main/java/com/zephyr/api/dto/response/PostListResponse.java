package com.zephyr.api.dto.response;

import com.zephyr.api.domain.Post;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PostListResponse {

    private Long postId;
    private String title;
    private String description;
    private LocalDate memoryDate;
    private LocalDateTime createdAt;
    private String thumbnailUrl;
    private MemberResponse author;
    private SeriesResponse series;
    private Integer memoryCount;

    public PostListResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.memoryDate = post.getMemoryDate();
        this.createdAt = post.getCreatedAt();
        this.author = new MemberResponse(post.getAuthor());
        this.series = new SeriesResponse(post.getSeries());
        this.memoryCount = post.getMemories().size();
    }
}
