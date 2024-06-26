package com.zephyr.api.controller;

import com.zephyr.api.config.TestConfig;
import com.zephyr.api.dto.request.*;
import com.zephyr.api.dto.response.*;
import com.zephyr.api.utils.H2TableCleaner;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.zephyr.api.utils.TestConstant.*;
import static com.zephyr.api.utils.TestStringUtils.createUrl;
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

    private AlbumResponse albumResponse;
    private SeriesResponse seriesResponse;

    @AfterEach
    void cleanUp() {
        h2TableCleaner.clean("post", "memory");
    }

    @BeforeAll
    void setUpAll() {
        MemberCreateRequest request = new MemberCreateRequest(
                "test@example.com",
                "username",
                "http://profile.url");
        restTemplate.postForEntity(
                createUrl(port, "/members"),
                request,
                MemberResponse.class);

        AlbumCreateRequest albumCreateRequest = new AlbumCreateRequest(
                TEST_ALBUM_TITLE,
                TEST_ALBUM_DESC,
                TEST_ALBUM_THUMBNAIL
        );
        albumResponse = restTemplate.postForEntity(
                createUrl(port, "/albums"),
                albumCreateRequest,
                AlbumResponse.class
        ).getBody();

        SeriesCreateRequest seriesCreateRequest = new SeriesCreateRequest(
                albumResponse.getId(),
                TEST_SERIES_NAME
        );
        seriesResponse = restTemplate.postForEntity(
                createUrl(port, "/series"),
                seriesCreateRequest,
                SeriesResponse.class
        ).getBody();
    }

    private List<MemoryCreateRequest> makeMemoryRequests(int size) {
        List<MemoryCreateRequest> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MemoryCreateRequest request = new MemoryCreateRequest(
                    (double) i,
                    "Content URL " + i,
                    "Caption " + i
            );
            result.add(request);
        }

        return result;
    }

    private PostCreateRequest makePostCreateRequest(Long albumId, Long seriesId, int thumbnailMemoryIndex, List<MemoryCreateRequest> memoryCreateRequests) {
        return new PostCreateRequest(
                albumId,
                seriesId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.now(),
                memoryCreateRequests.get(thumbnailMemoryIndex).getContentUrl(),
                memoryCreateRequests
        );
    }

    private ResponseEntity<PostResponse> createPost(PostCreateRequest request) {
        return restTemplate.postForEntity(
                createUrl(port, "/posts"),
                request,
                PostResponse.class
        );
    }

    private PostResponse updatePost(Long postId, PostUpdateRequest postUpdateRequest) {
        return restTemplate.patchForObject(
                createUrl(port, "/posts/" + postId),
                postUpdateRequest,
                PostResponse.class
        );
    }

    @Test
    @DisplayName("포스트 생성 요청 성공")
    public void successPostCreateRequest() {
        //given
        PostCreateRequest request = makePostCreateRequest(
                albumResponse.getId(),
                seriesResponse.getId(),
                0,
                makeMemoryRequests(5)
        );

        //when
        ResponseEntity<PostResponse> responseEntity = createPost(request);
        PostResponse postResponse = responseEntity.getBody();

        //then
        //포스트 내용 비교
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(postResponse.getId());
        assertEquals(request.getTitle(), postResponse.getTitle());
        assertEquals(request.getDescription(), postResponse.getDescription());
        assertEquals(request.getMemoryDate(), postResponse.getMemoryDate());
        assertEquals(seriesResponse.getName(), postResponse.getSeries());
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
        PostCreateRequest postCreateRequest = makePostCreateRequest(
                albumResponse.getId(),
                seriesResponse.getId(),
                0,
                makeMemoryRequests(5)
        );
        ResponseEntity<PostResponse> responseEntity = createPost(postCreateRequest);
        PostResponse postResponse = responseEntity.getBody();

        //when
        List<MemoryUpdateRequest> memoryUpdateRequests = postResponse.getMemories()
                .stream()
                .map(request -> new MemoryUpdateRequest(request.getId(), "수정된 캡션", request.getIndex() + 1))
                .toList();

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                null,
                "수정된 제목",
                "수정된 소개문",
                LocalDate.of(2024, 6, 10),
                postResponse.getMemories().get(1).getContentUrl(),
                memoryUpdateRequests
        );

        PostResponse response = updatePost(postResponse.getId(), postUpdateRequest);

        //then
        assertNotNull(response);
        assertEquals(postUpdateRequest.getTitle(), response.getTitle());
        assertEquals(postUpdateRequest.getDescription(), response.getDescription());
        assertEquals(postUpdateRequest.getMemoryDate(), response.getMemoryDate());
        assertEquals(postUpdateRequest.getThumbnailUrl(), response.getThumbnailUrl());
        assertEquals(postUpdateRequest.getMemoryDate(), response.getMemoryDate());

        //메모리 인덱스 비교
        for (MemoryUpdateRequest updateRequest : memoryUpdateRequests) {
            MemoryResponse updatedMemory = response.getMemories().stream()
                    .filter(memory -> memory.getId().equals(updateRequest.getId()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(updateRequest.getIndex(), updatedMemory.getIndex());
            assertEquals(updateRequest.getCaption(), updatedMemory.getCaption());
        }
    }

}
