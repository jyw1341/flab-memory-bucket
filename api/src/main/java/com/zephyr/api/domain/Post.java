package com.zephyr.api.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String title;

    @Setter
    private String description;

    @Setter
    private String thumbnailUrl;

    @Setter
    private LocalDate memoryDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

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

}
