package com.zephyr.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String username;

    @Setter
    private String email;

    @Setter
    private String profileImageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @Builder
    private Member(String username, String email, String profileImageUrl) {
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}
