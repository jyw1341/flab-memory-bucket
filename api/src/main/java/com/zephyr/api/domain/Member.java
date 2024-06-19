package com.zephyr.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String username;

    @Setter
    private String email;

    @Setter
    private String profileImageUrl;

    @Builder
    private Member(String username, String email, String profileImageUrl) {
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}
