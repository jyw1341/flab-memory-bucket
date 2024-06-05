package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Memory {

    private Long id;
    private Post post;
    private Integer index;
    private String caption;
    private String contentUrl;

    @Builder
    private Memory(Post post, Integer index, String caption, String contentUrl) {
        this.post = post;
        this.index = index;
        this.caption = caption;
        this.contentUrl = contentUrl;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}
