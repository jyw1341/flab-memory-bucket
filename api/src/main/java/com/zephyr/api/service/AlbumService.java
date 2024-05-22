package com.zephyr.api.service;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.AlbumMember;
import com.zephyr.api.domain.Member;
import com.zephyr.api.enums.AlbumAuthority;
import com.zephyr.api.enums.SubscribeStatus;
import com.zephyr.api.repository.AlbumMemberRepository;
import com.zephyr.api.repository.AlbumRepository;
import com.zephyr.api.repository.MemberRepository;
import com.zephyr.api.request.AlbumCreate;
import com.zephyr.api.request.AlbumMemberRequest;
import com.zephyr.api.request.AlbumUpdate;
import com.zephyr.api.response.AlbumListResponse;
import com.zephyr.api.response.AlbumMemberResponse;
import com.zephyr.api.response.AlbumResponse;
import com.zephyr.api.response.AlbumUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final MemberRepository memberRepository;
    private final AlbumMemberRepository albumMemberRepository;

    public Long create(Long userId, AlbumCreate albumCreate) {
        Member member = memberRepository.findById(userId);

        String thumbnailUrl = albumCreate.getThumbnailUrl();
        if (albumCreate.getThumbnailUrl() == null) {
            
        }

        Album album = Album.builder()
                .title(albumCreate.getTitle())
                .description(albumCreate.getDescription())
                .thumbnailUrl(albumCreate.getThumbnailUrl())
                .build();

        AlbumMember albumMember = AlbumMember.builder()
                .member(member)
                .album(album)
                .authority(AlbumAuthority.ADMIN)
                .status(SubscribeStatus.APPROVED)
                .build();

        albumMemberRepository.save(albumMember);
        albumRepository.save(album);

        return album.getId();
    }

    public AlbumResponse get(Long albumId, Long userId) {
        Member member = memberRepository.findById(userId);

        if (albumMemberRepository.findByAlbumIdAndMemberId(albumId, member.getId()) != null) {
            //TODO: 권한 없음 예외 처리
            throw new RuntimeException();
        }

        return albumRepository.findWithSubscribeAndMemory(albumId);
    }

    public List<AlbumListResponse> getList(Long memberId) {
        return albumRepository.findAllWithSubscribeAndMemory(memberId);
    }

    public AlbumUpdateResponse update(Long albumId, Long memberId, AlbumUpdate albumUpdate) {
        AlbumMember albumMember = albumMemberRepository.findByAlbumIdAndMemberId(albumId, memberId);

        if (albumMember == null) {
            //TODO: AlbumNotFound 예외 처리
            throw new RuntimeException();
        }

        if (!albumMember.getAuthority().equals(AlbumAuthority.ADMIN)) {
            //TODO: 권한 없음 예외 처리
            throw new RuntimeException();
        }

        Album album = albumMember.getAlbum();
        album.update(albumUpdate);

        return new AlbumUpdateResponse(album);
    }

    public void delete(Long albumId, Long loginId) {
        validateAdmin(albumId, loginId);

        albumRepository.delete(albumId);
    }

    public void createAlbumMember(Long albumId, Long ownerId, AlbumMemberRequest request) {
        Member candidate = memberRepository.findById(request.getMemberId());
        if (candidate == null) {
            //TODO: 없는 멤버
            throw new RuntimeException();
        }

        Album album = albumRepository.findById(albumId);
        if (album == null) {
            //TODO: 존재 하지 않는 앨범
            throw new RuntimeException();
        }

        AlbumMember albumMember = albumMemberRepository.findByAlbumIdAndMemberId(album.getId(), candidate.getId());
        if (albumMember != null && albumMember.getStatus().equals(SubscribeStatus.APPROVED)) {
            //TODO: 잘못된 요청. 이미 등록된 멤버
            throw new RuntimeException();
        }
        validateAdmin(album.getId(), ownerId);

        albumMemberRepository.save(AlbumMember.builder()
                .member(candidate)
                .album(album)
                .status(SubscribeStatus.PENDING)
                .authority(AlbumAuthority.USER)
                .build());
    }

    public List<AlbumMemberResponse> getAlbumMembers(Long albumId) {
        List<AlbumMember> albumMembers = albumMemberRepository.findByAlbumId(albumId);
        return albumMembers.stream().map(AlbumMemberResponse::new).toList();
    }

    public void deleteAlbumMember(Long albumId, Long memberId, Long targetId) {
        validateAdmin(albumId, memberId);

        albumMemberRepository.delete(albumId, targetId);
    }

    private void validateAdmin(Long albumId, Long loginId) {
        AlbumMember albumMember = albumMemberRepository.findByAlbumIdAndMemberId(albumId, loginId);
        if (albumMember == null || !albumMember.getAuthority().equals(AlbumAuthority.ADMIN)) {
            //TODO: 권한 없음
            throw new RuntimeException();
        }
    }
}
