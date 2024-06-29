package com.zephyr.api.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
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

    @Setter
    private String name;

    @Setter
    private Integer postCount;

    @Setter
    private LocalDate firstDate;

    @Setter
    private LocalDate lastDate;

    @Setter
    private String thumbnailUrl;

    @Builder
    private Series(Album album, String name) {
        this.album = album;
        this.name = name;
    }
}
