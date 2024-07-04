package com.zephyr.api.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zephyr.api.dto.SeriesAggregationDto;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.zephyr.api.domain.QPost.post;
import static com.zephyr.api.domain.QSeries.series;

@RequiredArgsConstructor
public class SeriesRepositoryImpl implements SeriesCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<SeriesAggregationDto> findSeriesAggregationDto(Long seriesId) {
        return Optional.ofNullable(
                jpaQueryFactory.select(Projections.constructor(
                                        SeriesAggregationDto.class,
                                        series.id,
                                        series.id.count(),
                                        post.memoryDate.min(),
                                        post.memoryDate.max()
                                )
                        )
                        .from(series)
                        .where(series.id.eq(seriesId))
                        .join(post).on(series.id.eq(post.series.id))
                        .groupBy(series.id)
                        .fetchOne()
        );

    }

    @Override
    public Optional<String> findSeriesThumbnail(Long seriesId) {
        return Optional.ofNullable(
                jpaQueryFactory.select(post.thumbnailUrl)
                        .from(series)
                        .where(series.id.eq(seriesId))
                        .join(post).on(series.id.eq(post.series.id))
                        .orderBy(post.memoryDate.asc(), post.createdDate.asc())
                        .fetchFirst()
        );
    }
}
