package com.zephyr.api.repository;

import com.zephyr.api.domain.AlbumMember;
import com.zephyr.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumMemberRepository extends JpaRepository<AlbumMember, Long>, AlbumMemberCustomRepository {

    List<AlbumMember> findByAlbumId(Long albumId);

    List<AlbumMember> findByMember(Member member);

    void deleteByAlbumIdAndMemberId(Long albumId, Long memberId);
}
