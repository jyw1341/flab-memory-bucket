package com.zephyr.api.repository;

import com.zephyr.api.domain.AlbumMember;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlbumMemberRepository {

    public void save(AlbumMember albumMember) {

    }

    public AlbumMember findByAlbumIdAndMemberId(Long albumId, Long memberId) {
        return null;
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
