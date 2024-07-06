package com.zephyr.api.controller;

import com.zephyr.api.config.TestConfig;
import com.zephyr.api.dto.response.SeriesResponse;
import com.zephyr.api.utils.TestRestTemplateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "SeriesControllerTestSql.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Import(TestConfig.class)
class SeriesControllerTest {

    @Autowired
    private TestRestTemplateUtils testRestTemplateUtils;

    @Test
    @DisplayName("시리즈 생성 성공")
    void whenRequestCreateSeries_thenSaveSeries() {

    }

    @Test
    @DisplayName("시리즈 목록 조회")
    void testGetSeriesList() {
        List<SeriesResponse> series = testRestTemplateUtils.requestGetSeriesList(1L);

        assertNotNull(series);
    }
}
