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
    private LocalDate memoryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALBUM_ID", nullable = false)
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member author;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERIES_ID")
    private Series series;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMORY_ID", nullable = false)
    private Memory coverMemory;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Memory> memories = new ArrayList<>();

    @Builder
    private Post(Album album, Series series, Member author, String title, String description, LocalDate memoryDate) {
        this.album = album;
        this.series = series;
        this.author = author;
        this.title = title;
        this.description = description;
        this.memoryDate = memoryDate;
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
