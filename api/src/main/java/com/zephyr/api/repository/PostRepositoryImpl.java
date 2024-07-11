package com.zephyr.api.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zephyr.api.domain.Post;
import com.zephyr.api.dto.PostSearchServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

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
    public Page<Post> search(PostSearchServiceDto dto) {
        JPAQuery<Post> query = jpaQueryFactory.selectFrom(post)
                .where(albumIdEq(dto.getAlbumId()),
                        authorContains(dto.getAuthor()),
                        titleContains(dto.getTitle()),
                        seriesEq(dto.getSeriesId()))
                .leftJoin(post.author, member).fetchJoin()
                .leftJoin(post.series, series).fetchJoin()
                .leftJoin(post.coverMemory, memory).fetchJoin()
                .limit(dto.getPageable().getPageSize())
                .offset(dto.getPageable().getOffset());

        for (Sort.Order o : dto.getPageable().getSortOr(Sort.by("createdDate").descending())) {
            PathBuilder<? extends Post> pathBuilder = new PathBuilder<Post>(post.getType(), post.getMetadata());
            query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
        }

        List<Post> content = query.fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(post.id.count())
                .from(post)
                .where(albumIdEq(dto.getAlbumId()),
                        authorContains(dto.getAuthor()),
                        titleContains(dto.getTitle()),
                        seriesEq(dto.getSeriesId()));

        return PageableExecutionUtils.getPage(content, dto.getPageable(), () -> Optional.ofNullable(countQuery.fetchOne()).orElse(0L));
    }

    private BooleanExpression albumIdEq(Long albumId) {
        if (albumId == null) {
            return null;
        }
        return post.album.id.eq(albumId);
    }

    private BooleanExpression authorContains(String username) {
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

    private BooleanExpression seriesEq(Long seriesId) {
        if (seriesId == null) {
            return null;
        }
        return post.series.id.eq(seriesId);
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
                .leftJoin(post.author, member).fetchJoin()
                .leftJoin(post.series, series).fetchJoin()
                .leftJoin(post.memories, memory).fetchJoin()
                .fetchOne());
    }
}
