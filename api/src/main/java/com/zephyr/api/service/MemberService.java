package com.zephyr.api.service;

import com.zephyr.api.domain.Member;
import com.zephyr.api.exception.MemberNotFoundException;
import com.zephyr.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MessageSource messageSource;

    public Member get(Long loginId) {
        return memberRepository.findById(loginId)
                .orElseThrow(() -> new MemberNotFoundException(messageSource));
    }
}
