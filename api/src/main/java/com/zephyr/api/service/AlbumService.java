package com.zephyr.api.service;

import com.zephyr.api.config.S3Config;
import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.AlbumMember;
import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.mapper.AlbumMemberCreateMapper;
import com.zephyr.api.dto.service.*;
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

    public Album create(AlbumCreateServiceDto dto) {
        Member member = memberService.get(dto.getMemberId());
        Album album = Album.builder()
                .owner(member)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .thumbnailUrl(dto.getThumbnailUrl())
                .build();

        albumRepository.save(album);
        AlbumMemberCreateServiceDto serviceDto = AlbumMemberCreateMapper.INSTANCE.toAlbumMemberCreateServiceDto(
                album,
                member,
                AlbumMemberRole.ADMIN
        );
        albumMemberService.create(serviceDto);

        return album;
    }

    public Album get(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));

        setDefaultThumbnailUrlIfNull(album);

        return album;
    }

    public List<Album> getList(AlbumListServiceDto dto) {
        AlbumMemberListServiceDto albumMemberListServiceDto = new AlbumMemberListServiceDto(
                dto.getMemberId(),
                null,
                dto.getPage(),
                dto.getSize()
        );
        List<AlbumMember> albumMembers = albumMemberService.getList(albumMemberListServiceDto);
        List<Album> result = new ArrayList<>();

        for (AlbumMember albumMember : albumMembers) {
            Album album = albumMember.getAlbum();
            setDefaultThumbnailUrlIfNull(album);
            result.add(album);
        }

        return result;
    }

    public void update(AlbumUpdateServiceDto dto) {
        Album album = albumRepository.findById(dto.getAlbumId())
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));

        validAlbumOwner(album, dto.getMemberId());
        album.setTitle(dto.getTitle());
        album.setDescription(dto.getDescription());
        album.setThumbnailUrl(dto.getThumbnailUrl());
    }

    public void delete(AlbumDeleteServiceDto dto) {
        Album album = albumRepository.findById(dto.getAlbumId())
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));

        validAlbumOwner(album, dto.getMemberId());
        albumRepository.delete(album);
    }

    private void validAlbumOwner(Album album, Long memberId) {
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
