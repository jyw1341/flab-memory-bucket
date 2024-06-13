package com.zephyr.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Album {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member owner;

    @Setter
    @Column(nullable = false, length = 20)
    private String title;

    @Setter
    @Column(length = 40)
    private String description;

    @Setter
    private String thumbnailUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private Album(String title, Member owner, String description, String thumbnailUrl) {
        this.title = title;
        this.owner = owner;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }
}
