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

    public Member get(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(messageSource));
    }

    public Member get(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(messageSource));
    }
}
