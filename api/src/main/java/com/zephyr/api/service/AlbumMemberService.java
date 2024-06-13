package com.zephyr.api.service;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.AlbumMember;
import com.zephyr.api.domain.Member;
import com.zephyr.api.enums.AlbumMemberRole;
import com.zephyr.api.enums.AlbumMemberStatus;
import com.zephyr.api.exception.AlbumMemberNotFoundException;
import com.zephyr.api.exception.ForbiddenException;
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

    public void create(Album album, Member member, AlbumMemberRole role) {
        AlbumMember albumMember = AlbumMember.builder()
                .member(member)
                .album(album)
                .status(AlbumMemberStatus.APPROVED)
                .role(role)
                .build();
        albumMemberRepository.save(albumMember);
    }

    public AlbumMember get(Long albumId, Long memberId) {
        return albumMemberRepository.findByAlbumIdAndMemberId(albumId, memberId)
                .orElseThrow(() -> new AlbumMemberNotFoundException(messageSource));
    }

    public List<AlbumMember> getListByMemberId(Long memberId) {
        return albumMemberRepository.findByMemberId(memberId);
    }

    public List<AlbumMember> getListByAlbumId(Long albumId) {
        return albumMemberRepository.findByAlbumId(albumId);
    }

    public void delete(Long albumId, Long memberId) {
        albumMemberRepository.deleteByAlbumIdAndMemberId(albumId, memberId);
    }

    public void validAlbumMember(AlbumMember albumMember) {
        if (albumMember.getStatus().equals(AlbumMemberStatus.APPROVED)) {
            return;
        }

        throw new ForbiddenException(messageSource);
    }
}
