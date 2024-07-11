package com.zephyr.api.repository;

import com.zephyr.api.dto.SeriesAggregationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;

@RequiredArgsConstructor
public class SeriesRepositoryImpl implements SeriesCustomRepository {

    private final NamedParameterJdbcTemplate template;

    @Override
    public List<SeriesAggregationDto> findSeriesAggregationDto(Long albumId) {
        String sql = "SELECT S.ID, " +
                "       S.NAME, " +
                "       COUNT(P.ID) AS POST_COUNT, " +
                "       MIN(P.MEMORY_DATE) AS FIRST_MEMORY_DATE, " +
                "       MAX(P.MEMORY_DATE) AS LAST_MEMORY_DATE, " +
                "       (SELECT M.CONTENT_URL " +
                "        FROM POST P2 " +
                "        JOIN MEMORY M ON P2.MEMORY_ID = M.ID " +
                "        WHERE P2.SERIES_ID = S.ID " +
                "        ORDER BY P2.MEMORY_DATE ASC, P2.CREATED_DATE ASC " +
                "        FETCH FIRST 1 ROW ONLY) AS THUMBNAIL_URL " +
                "FROM SERIES S " +
                "JOIN POST P ON S.ID = P.SERIES_ID " +
                "WHERE S.ALBUM_ID = :albumId " +
                "GROUP BY S.ID";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("albumId", albumId);

        return template.query(sql, param, seriesPostDtoRowMapper());
    }

    private RowMapper<SeriesAggregationDto> seriesPostDtoRowMapper() {
        return BeanPropertyRowMapper.newInstance(SeriesAggregationDto.class);
    }
}
