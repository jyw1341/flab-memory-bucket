package com.zephyr.api.service;

import com.zephyr.api.domain.AlbumMember;
import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.service.AlbumMemberCreateServiceDto;
import com.zephyr.api.dto.service.AlbumMemberDeleteServiceDto;
import com.zephyr.api.dto.service.AlbumMemberListServiceDto;
import com.zephyr.api.repository.AlbumMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumMemberService {

    private final AlbumMemberRepository albumMemberRepository;
    private final MessageSource messageSource;

    public void create(AlbumMemberCreateServiceDto dto) {
        AlbumMember albumMember = AlbumMember.builder()
                .member(dto.getMember())
                .album(dto.getAlbum())
                .role(dto.getRole())
                .build();

        albumMemberRepository.save(albumMember);
    }

    public List<AlbumMember> getList(AlbumMemberListServiceDto dto) {
        return albumMemberRepository.findAlbumMembers(dto);
    }

    public List<AlbumMember> getListByMember(Member member) {
        return albumMemberRepository.findByMember(member);
    }

    public List<AlbumMember> getListByAlbumId(AlbumMemberListServiceDto dto) {
        return albumMemberRepository.findByAlbumId(dto.getAlbumId());
    }

    public void delete(AlbumMemberDeleteServiceDto dto) {
        albumMemberRepository.deleteByAlbumIdAndMemberId(dto.getAlbumId(), dto.getTargetId());
    }
}
