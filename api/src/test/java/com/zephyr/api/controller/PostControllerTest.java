package com.zephyr.api.controller;

import com.zephyr.api.config.TestConfig;
import com.zephyr.api.dto.request.*;
import com.zephyr.api.dto.response.AlbumResponse;
import com.zephyr.api.dto.response.MemoryResponse;
import com.zephyr.api.dto.response.PostResponse;
import com.zephyr.api.dto.response.SeriesResponse;
import com.zephyr.api.utils.H2TableCleaner;
import com.zephyr.api.utils.TestRestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static com.zephyr.api.utils.TestConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestConfig.class)
class PostControllerTest {

    @Autowired
    private H2TableCleaner h2TableCleaner;
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    private TestRestTemplateUtils testRestTemplateUtils;
    private AlbumResponse albumResponse;
    private SeriesResponse seriesResponse;

    @BeforeAll
    void setUpAll() {
        testRestTemplateUtils = new TestRestTemplateUtils(restTemplate, port);
        testRestTemplateUtils.requestCreateMember(new MemberCreateRequest(
                "test@example.com",
                "username",
                "http://profile.url"));
        albumResponse = testRestTemplateUtils.requestCreateAlbum(new AlbumCreateRequest(
                TEST_ALBUM_TITLE,
                TEST_ALBUM_DESC,
                TEST_ALBUM_THUMBNAIL));
        seriesResponse = testRestTemplateUtils.requestCreateSeries(new SeriesCreateRequest(
                albumResponse.getId(),
                TEST_SERIES_NAME));
    }

    @AfterEach
    void cleanUp() {
        h2TableCleaner.clean("post", "memory");
    }

    @Test
    @DisplayName("포스트 생성 요청 성공")
    public void successPostCreateRequest() {
        //given
        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(10);
        PostCreateRequest request = new PostCreateRequest(
                albumResponse.getId(),
                seriesResponse.getId(),
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.now(),
                memoryRequestDtos.get(0).getContentUrl(),
                memoryRequestDtos
        );

        //when
        PostResponse postResponse = testRestTemplateUtils.requestCreatePost(request);

        //then
        //포스트 내용 비교
        assertNotNull(postResponse.getId());
        assertEquals(request.getTitle(), postResponse.getTitle());
        assertEquals(request.getDescription(), postResponse.getDescription());
        assertEquals(request.getMemoryDate(), postResponse.getMemoryDate());
        assertEquals(request.getSeriesId(), postResponse.getSeries().getId());
        assertEquals(request.getThumbnailUrl(), postResponse.getThumbnailUrl());

        //메모리 비교
        assertEquals(request.getMemoryCreateRequests().size(), postResponse.getMemories().size());
        for (MemoryCreateRequest createRequest : request.getMemoryCreateRequests()) {
            MemoryResponse memoryResponse = postResponse.getMemories().stream()
                    .filter(memory -> memory.getIndex().equals(createRequest.getIndex()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(createRequest.getCaption(), memoryResponse.getCaption());
        }
    }

    @Test
    @DisplayName("포스트 수정 요청 성공")
    void successUpdatePost() {
        //given
        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(10);
        PostCreateRequest request = new PostCreateRequest(
                albumResponse.getId(),
                seriesResponse.getId(),
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.now(),
                memoryRequestDtos.get(0).getContentUrl(),
                memoryRequestDtos
        );
        PostResponse postResponse = testRestTemplateUtils.requestCreatePost(request);

        //when
        List<MemoryUpdateRequest> memoryUpdateRequests = postResponse.getMemories()
                .stream()
                .map(memoryResponse -> new MemoryUpdateRequest(memoryResponse.getId(), "수정된 캡션", memoryResponse.getIndex() + 1))
                .toList();
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                null,
                "수정된 제목",
                "수정된 소개문",
                LocalDate.of(2024, 6, 10),
                postResponse.getMemories().get(1).getContentUrl(),
                memoryUpdateRequests
        );
        testRestTemplateUtils.requestUpdatePost(postResponse.getId(), postUpdateRequest);
        PostResponse result = testRestTemplateUtils.requestGetPost(postResponse.getId());

        //then
        assertNotNull(result);
        assertEquals(postUpdateRequest.getTitle(), result.getTitle());
        assertEquals(postUpdateRequest.getDescription(), result.getDescription());
        assertEquals(postUpdateRequest.getMemoryDate(), result.getMemoryDate());
        assertEquals(postUpdateRequest.getThumbnailUrl(), result.getThumbnailUrl());
        assertEquals(postUpdateRequest.getMemoryDate(), result.getMemoryDate());

        //메모리 인덱스 비교
        for (MemoryUpdateRequest updateRequest : memoryUpdateRequests) {
            MemoryResponse updatedMemory = result.getMemories().stream()
                    .filter(memory -> memory.getId().equals(updateRequest.getId()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(updateRequest.getIndex(), updatedMemory.getIndex());
            assertEquals(updateRequest.getCaption(), updatedMemory.getCaption());
        }
    }

}
