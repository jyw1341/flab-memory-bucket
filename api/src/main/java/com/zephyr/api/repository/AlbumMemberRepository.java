package com.zephyr.api.repository;

import com.zephyr.api.domain.AlbumMember;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AlbumMemberRepository {

    public void save(AlbumMember albumMember) {

    }

    public Optional<AlbumMember> findByAlbumIdAndMemberId(Long albumId, Long memberId) {
        return Optional.empty();
    }

    public List<AlbumMember> findByMemberId(Long memberId) {
        return null;
    }

    public List<AlbumMember> findByAlbumId(Long albumId) {
        return null;
    }

    public void delete(Long albumId, Long memberId) {

    }
}
