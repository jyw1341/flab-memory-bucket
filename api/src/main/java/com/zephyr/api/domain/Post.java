package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Post {

    private Long id;
    private Album album;
    private Series series;
    private Member author;
    private String title;
    private String description;
    private LocalDate memoryDate;
    private String thumbnailUrl;
    private LocalDateTime createdAt;

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

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public void setAuthor(Member author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMemoryDate(LocalDate memoryDate) {
        this.memoryDate = memoryDate;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
