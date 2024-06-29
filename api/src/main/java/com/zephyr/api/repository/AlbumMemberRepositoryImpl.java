package com.zephyr.api.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zephyr.api.domain.AlbumMember;
import com.zephyr.api.dto.AlbumMemberListServiceDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.zephyr.api.domain.QAlbumMember.albumMember;

@RequiredArgsConstructor
public class AlbumMemberRepositoryImpl implements AlbumMemberCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<AlbumMember> findAlbumMembers(AlbumMemberListServiceDto dto) {
        return jpaQueryFactory.selectFrom(albumMember)
                .where(albumIdEq(dto.getAlbumId()), memberIdEq(dto.getMemberId()))
                .limit(dto.getSize())
                .offset(0)
                .fetch();
    }

    private BooleanExpression albumIdEq(Long albumId) {
        if (albumId == null) {
            return null;
        }
        return albumMember.album.id.eq(albumId);
    }

    private BooleanExpression memberIdEq(Long memberId) {
        if (memberId == null) {
            return null;
        }
        return albumMember.member.id.eq(memberId);
    }
}
