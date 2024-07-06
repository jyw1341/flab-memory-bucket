package com.zephyr.api.domain;

import com.zephyr.api.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memory extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    @Setter
    private Double index;

    @Setter
    private String caption;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    private String contentUrl;

    @Builder
    private Memory(Post post, ContentType contentType, Double index, String caption, String contentUrl) {
        this.post = post;
        this.index = index;
        this.contentType = contentType;
        this.caption = caption;
        this.contentUrl = contentUrl;
    }
}
