package com.zephyr.api.repository;

import com.zephyr.api.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepository {

    public Optional<Member> findById(Long id) {
        return Optional.empty();
    }

    public Optional<Member> findByUsername(String username) {
        return Optional.empty();
    }
}
