package com.zephyr.api.dto.response;

import com.zephyr.api.domain.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostListResponse {

    private Long postId;
    private String title;
    private MemberResponse author;
    private SeriesResponse series;
    private String description;
    private LocalDateTime createdDate;
    private LocalDate memoryDate;
    private String thumbnailUrl;

    public PostListResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.author = new MemberResponse(post.getAuthor());
        this.series = post.getSeries() != null ? new SeriesResponse(post.getSeries()) : null;
        this.description = post.getDescription();
        this.memoryDate = post.getMemoryDate();
        this.thumbnailUrl = post.getCoverMemory().getContentUrl();
        this.createdDate = post.getCreatedDate();
    }
}
