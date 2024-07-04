package com.zephyr.api.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zephyr.api.domain.Post;
import com.zephyr.api.dto.PostSearchServiceDto;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.zephyr.api.domain.QMember.member;
import static com.zephyr.api.domain.QMemory.memory;
import static com.zephyr.api.domain.QPost.post;
import static com.zephyr.api.domain.QSeries.series;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> search(PostSearchServiceDto dto) {
        return jpaQueryFactory.selectFrom(post)
                .where(albumIdEq(dto.getAlbumId()), usernameContains(dto.getUsername()), titleContains(dto.getTitle()))
                .limit(dto.getSize())
                .offset((long) (dto.getPage() - 1) * dto.getSize())
                .orderBy(post.id.desc())
                .fetch();
    }

    private BooleanExpression albumIdEq(Long albumId) {
        if (albumId == null) {
            return null;
        }
        return post.album.id.eq(albumId);
    }

    private BooleanExpression usernameContains(String username) {
        if (username == null) {
            return null;
        }
        return post.author.username.contains(username);
    }

    private BooleanExpression titleContains(String title) {
        if (title == null) {
            return null;
        }
        return post.title.contains(title);
    }

    @Override
    public Optional<Post> findByIdFetchMemories(Long postId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(post)
                .where(post.id.eq(postId))
                .join(post.memories, memory).fetchJoin()
                .fetchOne());
    }

    @Override
    public Optional<Post> findByIdFetchMemberAndSeriesAndMemories(Long postId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(post)
                .where(post.id.eq(postId))
                .join(post.author, member).fetchJoin()
                .join(post.series, series).fetchJoin()
                .join(post.memories, memory).fetchJoin()
                .fetchOne());
    }
}
