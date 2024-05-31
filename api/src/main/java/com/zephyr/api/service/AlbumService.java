package com.zephyr.api.service;

import com.zephyr.api.config.S3Config;
import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Member;
import com.zephyr.api.domain.Subscribe;
import com.zephyr.api.enums.AlbumAuthority;
import com.zephyr.api.enums.SubscribeStatus;
import com.zephyr.api.exception.*;
import com.zephyr.api.repository.AlbumRepository;
import com.zephyr.api.repository.MemberRepository;
import com.zephyr.api.repository.SubscribeRepository;
import com.zephyr.api.request.AlbumCreate;
import com.zephyr.api.request.AlbumUpdate;
import com.zephyr.api.request.SubscribeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final MemberRepository memberRepository;
    private final SubscribeRepository subscribeRepository;
    private final MessageSource messageSource;
    private final S3Config s3Config;

    public Long create(Long loginId, AlbumCreate albumCreate) {
        Member member = memberRepository.findById(loginId)
                .orElseThrow(() -> new MemberNotFoundException(messageSource));
        Album album = Album.builder()
                .title(albumCreate.getTitle())
                .owner(member)
                .description(albumCreate.getDescription())
                .thumbnailUrl(albumCreate.getThumbnailUrl())
                .build();

        albumRepository.save(album);

        return album.getId();
    }

    public Album get(Long albumId, Long loginId) {
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new AlbumNotFoundException(messageSource));

        validReadPermission(album, loginId);
        setDefaultThumbnailUrlIfNull(album);

        return album;
    }

    public List<Album> getList(Long loginId) {
        List<Album> ownAlbums = albumRepository.findByMemberId(loginId);
        List<Album> result = new ArrayList<>(ownAlbums);
        List<Subscribe> subscribes = subscribeRepository.findByMemberId(loginId);

        for (Subscribe subscribe : subscribes) {
            Album album = subscribe.getAlbum();
            result.add(album);
        }
        result.forEach(this::setDefaultThumbnailUrlIfNull);

        return result;
    }

    public Album update(Long albumId, Long loginId, AlbumUpdate albumUpdate) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));

        validWritePermission(album, loginId);
        album.setTitle(albumUpdate.getTitle());
        album.setDescription(albumUpdate.getDescription());
        album.setThumbnailUrl(albumUpdate.getThumbnailUrl());

        return album;
    }

    public void delete(Long albumId, Long loginId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));

        validWritePermission(album, loginId);
        albumRepository.delete(album);
    }

    public void createSubscribe(Long albumId, Long loginId, SubscribeRequest request) {
        Member candidate = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new MemberNotFoundException(messageSource));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));

        validWritePermission(album, loginId);
        Optional<Subscribe> subscribe = subscribeRepository.findByAlbumIdAndMemberId(album.getId(), candidate.getId());
        if (subscribe.isPresent()) {
            throw new SubscribeFailException(messageSource);
        }
        subscribeRepository.save(Subscribe.builder()
                .subscriber(candidate)
                .album(album)
                .status(SubscribeStatus.PENDING)
                .authority(AlbumAuthority.USER)
                .build());
    }

    public void approveSubscribe(Long albumId, Long loginId) {
        Subscribe subscribe = subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId)
                .orElseThrow(() -> new SubscribeNotFoundException(messageSource));

        if (!subscribe.getStatus().equals(SubscribeStatus.PENDING)) {
            throw new SubscribeFailException(messageSource);
        }
        subscribe.setStatus(SubscribeStatus.APPROVED);
    }

    public List<Subscribe> getSubscribers(Long albumId, Long loginId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));

        validReadPermission(album, loginId);

        return album.getSubscribes();
    }

    public void deleteSubscribe(Long albumId, Long loginId, Long targetId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));

        validWritePermission(album, loginId);
        subscribeRepository.delete(album.getId(), targetId);
    }

    //------------------------------------------------------------------------------------

    private void setDefaultThumbnailUrlIfNull(Album album) {
        if (album.getThumbnailUrl() == null || album.getThumbnailUrl().isBlank()) {
            album.setThumbnailUrl(s3Config.getDefaultThumbnailUrl());
        }
    }

    private void validWritePermission(Album album, Long loginId) {
        if (!album.getOwner().getId().equals(loginId)) {
            throw new ForbiddenException(messageSource);
        }
    }

    private void validReadPermission(Album album, Long loginId) {
        if (album.getOwner().getId().equals(loginId)) {
            return;
        }

        Subscribe subscribe = subscribeRepository.findByAlbumIdAndMemberId(album.getId(), loginId)
                .orElseThrow(() -> new SubscribeNotFoundException(messageSource));

        if (!subscribe.getStatus().equals(SubscribeStatus.APPROVED)) {
            throw new ForbiddenException(messageSource);
        }
    }
}
