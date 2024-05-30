package com.zephyr.api.service;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.AlbumMember;
import com.zephyr.api.domain.Member;
import com.zephyr.api.enums.AlbumAuthority;
import com.zephyr.api.enums.SubscribeStatus;
import com.zephyr.api.exception.DuplicatedException;
import com.zephyr.api.exception.ForbiddenException;
import com.zephyr.api.exception.NotFoundException;
import com.zephyr.api.repository.AlbumMemberRepository;
import com.zephyr.api.repository.AlbumRepository;
import com.zephyr.api.repository.MemberRepository;
import com.zephyr.api.request.AlbumCreate;
import com.zephyr.api.request.AlbumMemberRequest;
import com.zephyr.api.request.AlbumUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final MemberRepository memberRepository;
    private final AlbumMemberRepository albumMemberRepository;
    private final MessageSource ms;
    private final Environment env;

    public Album create(Long loginId, AlbumCreate albumCreate) {
        Member member = memberRepository.findById(loginId)
                .orElseThrow(() -> new NotFoundException(ms.getMessage("notFound.member", null, Locale.KOREA)));

        Album album = Album.builder()
                .title(albumCreate.getTitle())
                .description(albumCreate.getDescription())
                .thumbnailUrl(getDefaultThumbnailIfEmpty(albumCreate.getThumbnailUrl()))
                .build();

        AlbumMember albumMember = AlbumMember.builder()
                .member(member)
                .album(album)
                .authority(AlbumAuthority.ADMIN)
                .status(SubscribeStatus.APPROVED)
                .build();

        albumMemberRepository.save(albumMember);
        albumRepository.save(album);

        return album;
    }

    private String getDefaultThumbnailIfEmpty(String thumbnailUrl) {
        if (thumbnailUrl.isEmpty()) {
            return env.getProperty("custom.s3.end-point") + env.getProperty("custom.s3.bucket-name") + "/default-thumbnail/1.jpg";
        }
        return thumbnailUrl;
    }

    public Album get(Long albumId, Long loginId) {
        AlbumMember albumMember = albumMemberRepository.findByAlbumIdAndMemberId(albumId, loginId)
                .orElseThrow(() -> new ForbiddenException(ms.getMessage("forbidden.album", null, Locale.KOREA)));

        return albumMember.getAlbum();
    }

    public List<Album> getAlbumsOfMember(Long loginId) {
        Member candidate = memberRepository.findById(loginId)
                .orElseThrow(() -> new NotFoundException(ms.getMessage("notFound.member", null, Locale.KOREA)));

        List<AlbumMember> albumMembers = albumMemberRepository.findByMemberId(loginId);

        return albumMembers.stream().map(AlbumMember::getAlbum).toList();
    }

    public Album update(Long albumId, Long loginId, AlbumUpdate albumUpdate) {
        Album album = albumRepository.findByOwnerIdAndAlbumId(loginId, albumId)
                .orElseThrow(() -> new NotFoundException(ms.getMessage("notFound.album", null, Locale.KOREA)));

        //TODO: setter를 안쓰고 엔티티를 수정 하기 위해 엔티티에 메서드를 만들었는데 괜찮을까요
        album.update(albumUpdate);

        return album;
    }

    public void delete(Long albumId, Long loginId) {
        Album album = albumRepository.findByOwnerIdAndAlbumId(loginId, albumId)
                .orElseThrow(() -> new NotFoundException(ms.getMessage("notFound.album", null, Locale.KOREA)));

        albumRepository.delete(album);
    }

    public void createAlbumMember(Long albumId, Long loginId, AlbumMemberRequest request) {
        Member candidate = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new NotFoundException(ms.getMessage("notFound.member", null, Locale.KOREA)));

        Album album = albumRepository.findByOwnerIdAndAlbumId(loginId, albumId)
                .orElseThrow(() -> new NotFoundException(ms.getMessage("notFound.album", null, Locale.KOREA)));

        Optional<AlbumMember> albumMember = albumMemberRepository.findByAlbumIdAndMemberId(album.getId(), candidate.getId());
        if (albumMember.isPresent() && albumMember.get().getStatus().equals(SubscribeStatus.APPROVED)) {
            throw new DuplicatedException(ms.getMessage("duplicated.albumMember.approved", null, Locale.KOREA));
        }
        if (albumMember.isPresent() && albumMember.get().getStatus().equals(SubscribeStatus.PENDING)) {
            throw new DuplicatedException(ms.getMessage("duplicated.albumMember.pending", null, Locale.KOREA));
        }

        albumMemberRepository.save(AlbumMember.builder()
                .member(candidate)
                .album(album)
                .status(SubscribeStatus.PENDING)
                .authority(AlbumAuthority.USER)
                .build());
    }

    public List<AlbumMember> getAlbumMembers(Long albumId, Long loginId) {
        memberRepository.findById(loginId)
                .orElseThrow(() -> new NotFoundException(ms.getMessage("notFound.member", null, Locale.KOREA)));

        albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException(ms.getMessage("notFound.album", null, Locale.KOREA)));

        return albumMemberRepository.findByAlbumId(albumId);
    }

    public void deleteAlbumMember(Long albumId, Long loginId, Long targetId) {
        Album album = albumRepository.findByOwnerIdAndAlbumId(loginId, albumId)
                .orElseThrow(() -> new NotFoundException(ms.getMessage("notFound.album", null, Locale.KOREA)));

        albumMemberRepository.delete(album.getId(), targetId);
    }
}
