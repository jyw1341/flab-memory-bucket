package com.zephyr.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String title;

    @Setter
    private String description;

    @Setter
    private String thumbnailUrl;

    @Setter
    private LocalDate memoryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALBUM_ID")
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member author;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERIES_ID")
    private Series series;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Memory> memories = new ArrayList<>();

    @Builder
    private Post(Album album, Series series, Member author, String title, String description, LocalDate memoryDate, String thumbnailUrl) {
        this.album = album;
        this.series = series;
        this.author = author;
        this.title = title;
        this.description = description;
        this.memoryDate = memoryDate;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void addMemory(Memory memory) {
        memories.add(memory);
        memory.setPost(this);
    }

    public void removeMemory(Memory memory) {
        memories.remove(memory);
        memory.setPost(null);
    }
}
