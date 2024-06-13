package com.zephyr.api.domain;

import com.zephyr.api.enums.AlbumMemberRole;
import com.zephyr.api.enums.AlbumMemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlbumMember {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALBUM_ID")
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Enumerated(EnumType.STRING)
    private AlbumMemberRole role;

    @Enumerated(EnumType.STRING)
    private AlbumMemberStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private AlbumMember(Album album, Member member, AlbumMemberRole role, AlbumMemberStatus status) {
        this.album = album;
        this.member = member;
        this.role = role;
        this.status = status;
    }

    public void setRole(AlbumMemberRole role) {
        this.role = role;
    }

    public void setStatus(AlbumMemberStatus status) {
        this.status = status;
    }
}
