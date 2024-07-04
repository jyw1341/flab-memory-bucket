package com.zephyr.api.controller;

import com.zephyr.api.config.TestConfig;
import com.zephyr.api.dto.request.MemoryCreateRequest;
import com.zephyr.api.dto.request.MemoryUpdateRequest;
import com.zephyr.api.dto.request.PostCreateRequest;
import com.zephyr.api.dto.request.PostUpdateRequest;
import com.zephyr.api.dto.response.MemoryResponse;
import com.zephyr.api.dto.response.PostResponse;
import com.zephyr.api.dto.response.SeriesResponse;
import com.zephyr.api.utils.H2TableCleaner;
import com.zephyr.api.utils.TestRestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static com.zephyr.api.utils.TestConstant.TEST_POST_DESC;
import static com.zephyr.api.utils.TestConstant.TEST_POST_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@Sql(scripts = "PostControllerTestSql.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class PostControllerTest {

    @Autowired
    private H2TableCleaner h2TableCleaner;
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    private TestRestTemplateUtils testRestTemplateUtils;

    @BeforeEach
    void setUp() {
        testRestTemplateUtils = new TestRestTemplateUtils(restTemplate, port);
    }

    @AfterEach
    void cleanUp() {
        h2TableCleaner.clean("post", "memory");
    }

    @Test
    @DisplayName("포스트 생성 / 포스트 정보 저장 성공")
    public void whenCreatePost_thenSavePostInfo() {
        //given
        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(10);
        PostCreateRequest request = new PostCreateRequest(
                1L,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.now(),
                memoryRequestDtos.get(0).getContentUrl(),
                memoryRequestDtos
        );

        //when
        ResponseEntity<Void> response = testRestTemplateUtils.requestCreatePost(1L, request);
        PostResponse result = testRestTemplateUtils.requestGetPost(response.getHeaders().getLocation().getPath());

        //then
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getMemoryDate(), result.getMemoryDate());
        assertEquals(request.getSeriesId(), result.getSeries().getId());
        assertEquals(request.getThumbnailUrl(), result.getThumbnailUrl());
    }

    @Test
    @DisplayName("포스트 생성 / 메모리 저장 성공")
    public void whenCreatePost_thenSaveMemories() {
        //given
        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(10);
        PostCreateRequest request = new PostCreateRequest(
                1L,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.now(),
                memoryRequestDtos.get(0).getContentUrl(),
                memoryRequestDtos
        );

        //when
        ResponseEntity<Void> response = testRestTemplateUtils.requestCreatePost(1L, request);
        PostResponse result = testRestTemplateUtils.requestGetPost(response.getHeaders().getLocation().getPath());

        //then
        assertEquals(request.getMemoryCreateRequests().size(), result.getMemories().size());
        for (MemoryCreateRequest createRequest : request.getMemoryCreateRequests()) {
            MemoryResponse memoryResponse = result.getMemories().stream()
                    .filter(memory -> memory.getIndex().equals(createRequest.getIndex()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(createRequest.getCaption(), memoryResponse.getCaption());
            assertEquals(createRequest.getIndex(), memoryResponse.getIndex());
            assertEquals(createRequest.getContentUrl(), memoryResponse.getContentUrl());
        }
    }

    @Test
    @DisplayName("포스트 생성 / 시리즈의 포스트 집계 업데이트 성공")
    public void whenCreatePost_thenSeriesOfPostUpdate() {
        //given
        Long albumId = 1L;
        Long seriesId = 1L;
        SeriesResponse seriesBeforePostCreate = testRestTemplateUtils.requestGetSeries(seriesId);
        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(1);
        PostCreateRequest postCreateRequest1 = new PostCreateRequest(
                seriesId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2023, 12, 31),
                memoryRequestDtos.get(0).getContentUrl(),
                memoryRequestDtos
        );
        PostCreateRequest postCreateRequest2 = new PostCreateRequest(
                seriesId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 1, 1),
                memoryRequestDtos.get(0).getContentUrl(),
                memoryRequestDtos
        );

        //when
        testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest1);
        testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest2);
        SeriesResponse seriesAfterPostCreate = testRestTemplateUtils.requestGetSeries(seriesId);

        //then
        assertNotNull(seriesAfterPostCreate);
        assertEquals(seriesId, seriesAfterPostCreate.getId());
        assertEquals(seriesBeforePostCreate.getName(), seriesAfterPostCreate.getName());
        assertEquals(seriesBeforePostCreate.getPostCount() + 2, seriesAfterPostCreate.getPostCount());
        assertEquals(postCreateRequest1.getMemoryDate(), seriesAfterPostCreate.getFirstDate());
        assertEquals(postCreateRequest1.getThumbnailUrl(), seriesAfterPostCreate.getThumbnailUrl());
        assertEquals(postCreateRequest2.getMemoryDate(), seriesAfterPostCreate.getLastDate());
    }

    @Test
    @DisplayName("포스트 생성 / 모든 테스트 성공")
    void successCreatePost() {
        //given
        Long albumId = 1L;
        Long seriesId = 1L;
        SeriesResponse seriesBeforePostCreate = testRestTemplateUtils.requestGetSeries(seriesId);
        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(10);
        PostCreateRequest request = new PostCreateRequest(
                seriesId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.now(),
                memoryRequestDtos.get(0).getContentUrl(),
                memoryRequestDtos
        );

        //when
        ResponseEntity<Void> response = testRestTemplateUtils.requestCreatePost(albumId, request);
        PostResponse result = testRestTemplateUtils.requestGetPost(response.getHeaders().getLocation().getPath());
        SeriesResponse seriesAfterPostCreate = testRestTemplateUtils.requestGetSeries(seriesId);

        //then
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getMemoryDate(), result.getMemoryDate());
        assertEquals(request.getSeriesId(), result.getSeries().getId());
        assertEquals(request.getThumbnailUrl(), result.getThumbnailUrl());

        assertEquals(request.getMemoryCreateRequests().size(), result.getMemories().size());
        for (MemoryCreateRequest createRequest : request.getMemoryCreateRequests()) {
            MemoryResponse memoryResponse = result.getMemories().stream()
                    .filter(memory -> memory.getIndex().equals(createRequest.getIndex()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(createRequest.getCaption(), memoryResponse.getCaption());
            assertEquals(createRequest.getIndex(), memoryResponse.getIndex());
            assertEquals(createRequest.getContentUrl(), memoryResponse.getContentUrl());
        }

        assertNotNull(seriesAfterPostCreate);
        assertEquals(seriesId, seriesAfterPostCreate.getId());
        assertEquals(seriesBeforePostCreate.getName(), seriesAfterPostCreate.getName());
        assertEquals(seriesBeforePostCreate.getPostCount() + 1, seriesAfterPostCreate.getPostCount());
        assertEquals(request.getMemoryDate(), seriesAfterPostCreate.getFirstDate());
    }

    @Test
    @DisplayName("포스트 수정 / 포스트 정보 수정 성공")
    void successUpdatePostInfo() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;
        Long seriesTwoId = 2L;
        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getContentUrl(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest).getHeaders().getLocation().getPath();
        PostResponse postResponse = testRestTemplateUtils.requestGetPost(path);

        //when
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                seriesTwoId,
                "수정된 제목",
                "수정된 소개문",
                LocalDate.of(2024, 7, 2),
                postResponse.getMemories().get(1).getContentUrl()
        );
        testRestTemplateUtils.requestUpdatePost(postResponse.getId(), postUpdateRequest);
        PostResponse updatedPostResult = testRestTemplateUtils.requestGetPost(postResponse.getId());

        //then
        assertNotNull(updatedPostResult);
        assertEquals(postUpdateRequest.getTitle(), updatedPostResult.getTitle());
        assertEquals(postUpdateRequest.getSeriesId(), updatedPostResult.getSeries().getId());
        assertEquals(postUpdateRequest.getDescription(), updatedPostResult.getDescription());
        assertEquals(postUpdateRequest.getMemoryDate(), updatedPostResult.getMemoryDate());
        assertEquals(postUpdateRequest.getThumbnailUrl(), updatedPostResult.getThumbnailUrl());
        assertEquals(postUpdateRequest.getMemoryDate(), updatedPostResult.getMemoryDate());
    }

    @Test
    @DisplayName("포스트를 현재 시리즈의 최소 날짜로 수정")
    void whenUpdatePost_thenSeriesUpdate() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;

        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest postCreateRequest1 = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 10),
                memoryRequestDtos.get(0).getContentUrl(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest1).getHeaders().getLocation().getPath();
        PostResponse postCreateResult1 = testRestTemplateUtils.requestGetPost(path);

        PostCreateRequest postCreateRequest2 = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 5),
                memoryRequestDtos.get(1).getContentUrl(),
                memoryRequestDtos
        );
        testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest2);

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                seriesOneId,
                "수정된 제목",
                "수정된 소개문",
                LocalDate.of(2024, 7, 1),
                postCreateResult1.getMemories().get(0).getContentUrl()
        );
        testRestTemplateUtils.requestUpdatePost(postCreateResult1.getId(), postUpdateRequest);
        SeriesResponse seriesResult = testRestTemplateUtils.requestGetSeries(postCreateResult1.getId());

        //then
        assertNotNull(seriesResult);
        assertEquals(2, seriesResult.getPostCount());
        assertEquals(seriesOneId, seriesResult.getId());
        assertEquals(postUpdateRequest.getMemoryDate(), seriesResult.getFirstDate());
        assertEquals(postCreateRequest2.getMemoryDate(), seriesResult.getLastDate());
        assertEquals(postUpdateRequest.getThumbnailUrl(), seriesResult.getThumbnailUrl());
    }

    @Test
    @DisplayName("포스트를 현재 시리즈의 최대 날짜로 수정")
    void whenUpdatePostMemoryDate_thenSeriesUpdate() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;

        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest postCreateRequest1 = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getContentUrl(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest1).getHeaders().getLocation().getPath();
        PostResponse postResponse = testRestTemplateUtils.requestGetPost(path);

        PostCreateRequest postCreateRequest2 = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 5),
                memoryRequestDtos.get(1).getContentUrl(),
                memoryRequestDtos
        );
        testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest2);

        //when
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                seriesOneId,
                "수정된 제목",
                "수정된 소개문",
                LocalDate.of(2024, 7, 10),
                postResponse.getMemories().get(0).getContentUrl()
        );
        testRestTemplateUtils.requestUpdatePost(postResponse.getId(), postUpdateRequest);
        SeriesResponse seriesResult = testRestTemplateUtils.requestGetSeries(postResponse.getId());

        //then
        assertNotNull(seriesResult);
        assertEquals(2, seriesResult.getPostCount());
        assertEquals(seriesOneId, seriesResult.getId());
        assertEquals(postUpdateRequest.getMemoryDate(), seriesResult.getLastDate());
        assertEquals(postCreateRequest2.getMemoryDate(), seriesResult.getFirstDate());
        assertEquals(postCreateRequest2.getThumbnailUrl(), seriesResult.getThumbnailUrl());
    }

    @Test
    @DisplayName("포스트에 메모리 추가")
    void whenAddMemories_thenSuccess() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;

        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest postCreateRequest1 = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getContentUrl(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest1).getHeaders().getLocation().getPath();
        PostResponse postResponse = testRestTemplateUtils.requestGetPost(path);

        List<MemoryUpdateRequest> memoryUpdateRequests = List.of(
                new MemoryUpdateRequest(postResponse.getMemories().get(0).getId(), 0.0, "메모리 수정1", "URL1"),
                new MemoryUpdateRequest(postResponse.getMemories().get(1).getId(), 1.0, "메모리 수정2", "URL2"),
                new MemoryUpdateRequest(null, 2.0, "메모리 생성1", "URL3"),
                new MemoryUpdateRequest(null, 3.0, "메모리 생성2", "URL4")
        );

        testRestTemplateUtils.requestUpdateMemories(postResponse.getId(), memoryUpdateRequests);
        PostResponse result = testRestTemplateUtils.requestGetPost(path);

        assertNotNull(result);
    }
}
