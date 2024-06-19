package com.zephyr.api.domain;

import com.zephyr.api.enums.AlbumMemberRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlbumMember extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALBUM_ID")
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Setter
    @Enumerated(EnumType.STRING)
    private AlbumMemberRole role;

    @Builder
    private AlbumMember(Album album, Member member, AlbumMemberRole role) {
        this.album = album;
        this.member = member;
        this.role = role;
    }
}
