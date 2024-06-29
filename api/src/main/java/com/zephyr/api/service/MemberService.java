package com.zephyr.api.service;

import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.service.MemberCreateServiceDto;
import com.zephyr.api.dto.service.MemberUpdateServiceDto;
import com.zephyr.api.exception.MemberNotFoundException;
import com.zephyr.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MessageSource messageSource;

    public Member create(MemberCreateServiceDto dto) {
        Member member = Member.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .profileUrl(dto.getProfileUrl())
                .build();

        validateDuplicateMember(member);
        memberRepository.save(member);

        return member;
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public Member get(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(messageSource));
    }

    @Transactional
    public void update(MemberUpdateServiceDto dto) {
        Member member = get(dto.getMemberId());

        member.setUsername(dto.getUsername());
        member.setProfileUrl(dto.getProfileUrl());
    }
}
