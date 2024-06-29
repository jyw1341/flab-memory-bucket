package com.zephyr.api.service;

import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.MemberCreateServiceDto;
import com.zephyr.api.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MemberService memberService;


    @Test
    @DisplayName("회원 생성 성공")
    void createMemberSuccess() {
        //given
        MemberCreateServiceDto dto = new MemberCreateServiceDto(
                "test@example.com",
                "username",
                "http://profile.url");
        Member member = Member.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .profileUrl(dto.getProfileUrl())
                .build();

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        //when
        Member createdMember = memberService.create(dto);

        //then
        assertEquals(dto.getEmail(), createdMember.getEmail());
        assertEquals(dto.getUsername(), createdMember.getUsername());
        assertEquals(dto.getProfileUrl(), createdMember.getProfileUrl());
        verify(memberRepository, times(1)).findByEmail(dto.getEmail());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("이미 존재하는 회원으로 인해 생성 실패")
    void givenDuplicatedEmail_whenCreateMember_Fail() {
        //given
        MemberCreateServiceDto dto = new MemberCreateServiceDto(
                "test@example.com",
                "username",
                "http://profile.url");
        Member existingMember = Member.builder()
                .email(dto.getEmail())
                .username("existingUsername")
                .profileUrl("http://existing.profile.url")
                .build();

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(existingMember));

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> memberService.create(dto));

        //then
        assertEquals("이미 존재하는 회원입니다.", exception.getMessage());
        verify(memberRepository, times(1)).findByEmail(dto.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
    }
}
