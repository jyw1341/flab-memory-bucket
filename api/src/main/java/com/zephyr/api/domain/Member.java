package com.zephyr.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String username;

    @Setter
    private String email;

    @Setter
    private String profileUrl;

    @Builder
    private Member(Long id, String username, String email, String profileUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileUrl = profileUrl;
    }
}
