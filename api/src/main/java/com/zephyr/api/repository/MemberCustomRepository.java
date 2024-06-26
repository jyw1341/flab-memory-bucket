package com.zephyr.api.repository;

import com.zephyr.api.domain.Member;

import java.util.Optional;

public interface MemberCustomRepository {

    Optional<Member> findByEmail(String email);
}
