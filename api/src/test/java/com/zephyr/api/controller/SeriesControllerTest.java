package com.zephyr.api.controller;

import com.zephyr.api.config.TestConfig;
import com.zephyr.api.dto.request.SeriesCreateRequest;
import com.zephyr.api.dto.response.SeriesResponse;
import com.zephyr.api.utils.TestRestTemplateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        //given
        SeriesCreateRequest seriesCreateRequest = new SeriesCreateRequest("시리즈 1");

        //when
        ResponseEntity<Void> seriesCreateResponse
                = testRestTemplateUtils.requestCreateSeries(1L, seriesCreateRequest);
        String path = seriesCreateResponse.getHeaders().getLocation().getPath();
        SeriesResponse seriesResponse = testRestTemplateUtils.requestGetSeries(path);

        //then
        assertNotNull(seriesCreateResponse);
        assertEquals(HttpStatus.CREATED, seriesCreateResponse.getStatusCode());
        assertEquals(seriesCreateRequest.getSeriesName(), seriesResponse.getName());
    }

    @Test
    @DisplayName("앨범의 시리즈 목록을 조회하면 앨범의 모든 시리즈를 반환한다")
    void testGetSeriesList() {
        //given
        List<SeriesResponse> seriesListBeforeNewCreate = testRestTemplateUtils.requestGetSeriesList(1L);
        List<SeriesCreateRequest> seriesCreateRequests = testRestTemplateUtils.makeSeriesCreateRequest(5);
        testRestTemplateUtils.requestCreateSeries(1L, seriesCreateRequests);

        //when
        List<SeriesResponse> seriesResponses = testRestTemplateUtils.requestGetSeriesList(1L);

        //then
        assertEquals(seriesListBeforeNewCreate.size() + seriesCreateRequests.size(), seriesResponses.size());
    }
}
