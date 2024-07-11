package com.zephyr.api.dto.response;

import com.zephyr.api.domain.Post;
import com.zephyr.api.dto.PostListDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
public class PostSearchResponse {

    private Long total;
    private Integer pageNumber;
    private Integer pageSize;
    private List<PostListDto> content;

    public PostSearchResponse(Page<Post> page) {
        this.total = page.getTotalElements();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.content = page.getContent().stream().map(PostListDto::new).toList();
    }
}
