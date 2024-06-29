package com.zephyr.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Series extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALBUM_ID")
    private Album album;

    @OneToMany(mappedBy = "series")
    private List<Post> posts;

    @Setter
    private String name;

    @Builder
    private Series(Album album, String name) {
        this.album = album;
        this.name = name;
    }
}
