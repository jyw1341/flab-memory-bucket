package com.zephyr.api.service;

import com.zephyr.api.config.S3Config;
import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.AlbumMember;
import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.AlbumUpdateDTO;
import com.zephyr.api.dto.request.AlbumCreateRequest;
import com.zephyr.api.enums.AlbumMemberRole;
import com.zephyr.api.exception.AlbumNotFoundException;
import com.zephyr.api.exception.ForbiddenException;
import com.zephyr.api.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final MemberService memberService;
    private final AlbumMemberService albumMemberService;
    private final MessageSource messageSource;
    private final S3Config s3Config;

    public Album create(Member member, AlbumCreateRequest dto) {
        Album album = Album.builder()
                .owner(member)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .thumbnailUrl(dto.getThumbnailUrl())
                .build();

        albumRepository.save(album);
        albumMemberService.create(album, member, AlbumMemberRole.ADMIN);

        return album;
    }

    public Album get(Member member, Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));
        setDefaultThumbnailUrlIfNull(album);

        return album;
    }

    public Album get(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));
    }

    public List<Album> getList(Long memberId) {
        List<AlbumMember> albumMembers = albumMemberService.getListByMemberId(memberId);
        List<Album> result = new ArrayList<>();

        for (AlbumMember albumMember : albumMembers) {
            Album album = albumMember.getAlbum();
            setDefaultThumbnailUrlIfNull(album);
            result.add(album);
        }

        return result;
    }

    public Album update(AlbumUpdateDTO dto) {
        Album album = albumRepository.findById(dto.getAlbumId())
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));
        validAlbumOwner(album, dto.getMemberId());

        album.setTitle(dto.getTitle());
        album.setDescription(dto.getDescription());
        album.setThumbnailUrl(dto.getThumbnailUrl());

        return album;
    }

    public void delete(Long albumId, Long memberId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));

        validAlbumOwner(album, memberId);
        albumRepository.delete(album);
    }

    public AlbumMember getAlbumMember(Long albumId, Long memberId) {
        return albumMemberService.get(albumId, memberId);
    }

    public List<AlbumMember> getAlbumMembers(Long albumId, Long memberId) {
        AlbumMember albumMember = albumMemberService.get(albumId, memberId);

        albumMemberService.validAlbumMember(albumMember);

        return albumMemberService.getListByAlbumId(albumId);
    }

    public void banAlbumMember(Long albumId, Long ownerId, Long targetId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));

        validAlbumOwner(album, ownerId);
        albumMemberService.delete(albumId, targetId);
    }

    public void validAlbumOwner(Album album, Long memberId) {
        if (album.getOwner().getId().equals(memberId)) {
            return;
        }
        throw new ForbiddenException(messageSource);
    }

    private void setDefaultThumbnailUrlIfNull(Album album) {
        if (album.getThumbnailUrl() == null || album.getThumbnailUrl().isBlank()) {
            album.setThumbnailUrl(s3Config.getDefaultThumbnailUrl());
        }
    }
}
