package com.zephyr.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memory extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    private Double index;

    private String caption;

    private String contentUrl;

    @Builder
    private Memory(Post post, Double index, String caption, String contentUrl) {
        this.post = post;
        this.index = index;
        this.caption = caption;
        this.contentUrl = contentUrl;
    }

    public void setIndex(Double index) {
        this.index = index;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
