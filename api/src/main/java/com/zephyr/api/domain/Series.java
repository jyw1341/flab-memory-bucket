package com.zephyr.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Series extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALBUM_ID")
    private Album album;

    @Setter
    private String name;

    @Setter
    private Long postCount;

    @Setter
    private LocalDate firstDate;

    @Setter
    private LocalDate lastDate;

    @Setter
    private String thumbnailUrl;

    @Builder
    private Series(Album album, String name, Long postCount) {
        this.album = album;
        this.name = name;
        this.postCount = postCount;
    }
}
