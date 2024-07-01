package com.zephyr.api.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zephyr.api.domain.Series;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.zephyr.api.domain.QPost.post;
import static com.zephyr.api.domain.QSeries.series;

@RequiredArgsConstructor
public class SeriesRepositoryImpl implements SeriesCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Series> findSeriesPost(Long albumId) {
        return jpaQueryFactory
                .selectFrom(series)
                .where(series.album.id.eq(albumId))
                .join(series.posts, post).fetchJoin()
                .orderBy(post.memoryDate.asc(), post.createdDate.asc())
                .fetch();
    }
}
