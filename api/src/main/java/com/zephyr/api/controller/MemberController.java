package com.zephyr.api.controller;

import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.mapper.MemberCreateMapper;
import com.zephyr.api.dto.request.MemberCreateRequest;
import com.zephyr.api.dto.response.MemberResponse;
import com.zephyr.api.dto.service.MemberCreateServiceDto;
import com.zephyr.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public MemberResponse create(@RequestBody MemberCreateRequest request) {
        MemberCreateServiceDto serviceDto = MemberCreateMapper.INSTANCE.toMemberCreateServiceDto(request);
        Member member = memberService.create(serviceDto);

        return new MemberResponse(member);
    }
}
