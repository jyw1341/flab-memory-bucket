package com.zephyr.api.controller;

import com.zephyr.api.config.TestConfig;
import com.zephyr.api.dto.request.MemoryCreateRequest;
import com.zephyr.api.dto.request.MemoryUpdateRequest;
import com.zephyr.api.dto.request.PostCreateRequest;
import com.zephyr.api.dto.request.PostUpdateRequest;
import com.zephyr.api.dto.response.MemoryResponse;
import com.zephyr.api.dto.response.PostListResponse;
import com.zephyr.api.dto.response.PostResponse;
import com.zephyr.api.utils.H2TableCleaner;
import com.zephyr.api.utils.RestPageImpl;
import com.zephyr.api.utils.TestRestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zephyr.api.utils.TestConstant.TEST_POST_DESC;
import static com.zephyr.api.utils.TestConstant.TEST_POST_TITLE;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)

@Sql(scripts = "PostControllerTestSql.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class PostControllerEndToEndTest {

    @Autowired
    private TestRestTemplateUtils testRestTemplateUtils;

    @Autowired
    private H2TableCleaner tableCleaner;

    @AfterEach
    void cleanUp() {
//        tableCleaner.cleanTables("post", "memory");
    }

    @Test
    @DisplayName("포스트를 생성 시 포스트 정보가 저장된다")
    public void createPost_shouldSavePostInfo() {
        //given
        List<MemoryCreateRequest> memoryCreateRequests = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest request = new PostCreateRequest(
                1L,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.now(),
                memoryCreateRequests.get(0).getRequestId(),
                memoryCreateRequests
        );

        //when
        ResponseEntity<Void> response = testRestTemplateUtils.requestCreatePost(1L, request);
        PostResponse result = testRestTemplateUtils.requestGetPost(response.getHeaders().getLocation().getPath());

        //then
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getMemoryDate(), result.getMemoryDate());
        assertEquals(request.getSeriesId(), result.getSeries().getId());
        assertEquals(request.getMemoryCreateRequests().get(0).getContentUrl(), result.getThumbnailUrl());
    }

    @Test
    @DisplayName("포스트를 생성하면 포스트에 속한 메모리가 저장된다")
    public void createPost_shouldSaveMemories() {
        //given
        List<MemoryCreateRequest> memoryCreateRequests = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest request = new PostCreateRequest(
                1L,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.now(),
                memoryCreateRequests.get(0).getRequestId(),
                memoryCreateRequests
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
    @DisplayName("포스트 제목, 소개문, 기억 날짜를 수정할 수 있다")
    void updatePost_shouldUpdatePostInfo() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;
        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getRequestId(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest).getHeaders().getLocation().getPath();
        PostResponse postResponse = testRestTemplateUtils.requestGetPost(path);

        //when
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                seriesOneId,
                postResponse.getMemories().get(1).getId(),
                "수정된 제목",
                "수정된 소개문",
                LocalDate.of(2024, 7, 2)
        );
        testRestTemplateUtils.requestUpdatePost(postResponse.getId(), postUpdateRequest);
        PostResponse updatedPostResult = testRestTemplateUtils.requestGetPost(postResponse.getId());

        //then
        assertNotNull(updatedPostResult);
        assertEquals(postResponse.getId(), updatedPostResult.getId());
        assertEquals(postUpdateRequest.getTitle(), updatedPostResult.getTitle());
        assertEquals(postUpdateRequest.getSeriesId(), updatedPostResult.getSeries().getId());
        assertEquals(postUpdateRequest.getDescription(), updatedPostResult.getDescription());
        assertEquals(postUpdateRequest.getMemoryDate(), updatedPostResult.getMemoryDate());
    }

    @Test
    @DisplayName("포스트에 설정한 시리즈를 다른 시리즈로 수정할 수 있다")
    void updateSeriesOfPostToAnotherSeries() {
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
                memoryRequestDtos.get(0).getRequestId(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest).getHeaders().getLocation().getPath();
        PostResponse postResponse = testRestTemplateUtils.requestGetPost(path);

        //when
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                seriesTwoId,
                postResponse.getMemories().get(1).getId(),
                "수정된 제목",
                "수정된 소개문",
                LocalDate.of(2024, 7, 2)
        );
        testRestTemplateUtils.requestUpdatePost(postResponse.getId(), postUpdateRequest);
        PostResponse updatedPostResult = testRestTemplateUtils.requestGetPost(postResponse.getId());

        //then
        assertNotNull(updatedPostResult);
        assertEquals(postResponse.getId(), updatedPostResult.getId());
        assertEquals(postUpdateRequest.getSeriesId(), updatedPostResult.getSeries().getId());
    }

    @Test
    @DisplayName("포스트의 시리즈를 설정 해제 할 수 있다")
    void updateSeriesOfPostToNull() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;
        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getRequestId(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest).getHeaders().getLocation().getPath();
        PostResponse postResponse = testRestTemplateUtils.requestGetPost(path);

        //when
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                null,
                postResponse.getMemories().get(1).getId(),
                "수정된 제목",
                "수정된 소개문",
                LocalDate.of(2024, 7, 1)
        );
        testRestTemplateUtils.requestUpdatePost(postResponse.getId(), postUpdateRequest);
        PostResponse updatedPostResult = testRestTemplateUtils.requestGetPost(postResponse.getId());

        //then
        assertNotNull(updatedPostResult);
        assertEquals(postResponse.getId(), updatedPostResult.getId());
        assertNull(updatedPostResult.getSeries());
    }

    @Test
    @DisplayName("시리즈가 설정되지 않은 포스트에 시리즈를 설정할 수 있다")
    void updateSetSeriesOfPost() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;
        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                null,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getRequestId(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest).getHeaders().getLocation().getPath();
        PostResponse postResponse = testRestTemplateUtils.requestGetPost(path);

        //when
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                seriesOneId,
                postResponse.getMemories().get(1).getId(),
                "수정된 제목",
                "수정된 소개문",
                LocalDate.of(2024, 7, 1)
        );
        testRestTemplateUtils.requestUpdatePost(postResponse.getId(), postUpdateRequest);
        PostResponse updatedPostResult = testRestTemplateUtils.requestGetPost(postResponse.getId());

        //then
        assertNotNull(updatedPostResult);
        assertEquals(postResponse.getId(), updatedPostResult.getId());
        assertEquals(postUpdateRequest.getSeriesId(), updatedPostResult.getSeries().getId());
    }

    @Test
    @DisplayName("포스트의 커버 메모리를 다른 메모리로 수정할 수 있다")
    void updatePostCoverMemory() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;
        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getRequestId(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, postCreateRequest).getHeaders().getLocation().getPath();
        PostResponse postResponse = testRestTemplateUtils.requestGetPost(path);

        //when
        MemoryResponse newCoverMemory = postResponse.getMemories().get(1);
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                seriesOneId,
                newCoverMemory.getId(),
                "수정된 제목",
                "수정된 소개문",
                LocalDate.of(2024, 7, 1)
        );
        testRestTemplateUtils.requestUpdatePost(postResponse.getId(), postUpdateRequest);
        PostResponse updatedPostResult = testRestTemplateUtils.requestGetPost(postResponse.getId());

        //then
        assertNotNull(updatedPostResult);
        assertEquals(postResponse.getId(), updatedPostResult.getId());
        assertEquals(newCoverMemory.getContentUrl(), updatedPostResult.getThumbnailUrl());
    }

    @Test
    @DisplayName("포스트에 속한 메모리들의 캡션과 인덱스를 변경할 수 있다")
    void updateMemoriesInfoOfPost() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;

        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest createRequest = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getRequestId(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, createRequest).getHeaders().getLocation().getPath();
        PostResponse post = testRestTemplateUtils.requestGetPost(path);

        List<MemoryUpdateRequest> memoryUpdateRequests = post.getMemories()
                .stream()
                .map(memory -> new MemoryUpdateRequest(
                        memory.getId(),
                        memory.getIndex() + 1,
                        "수정됨" + memory.getCaption(),
                        memory.getContentUrl()))
                .toList();

        testRestTemplateUtils.requestUpdateMemories(post.getId(), memoryUpdateRequests);
        PostResponse updatedPost = testRestTemplateUtils.requestGetPost(path);

        assertNotNull(updatedPost);
        assertEquals(post.getId(), updatedPost.getId());
        assertEquals(memoryUpdateRequests.size(), updatedPost.getMemories().size());
        for (MemoryUpdateRequest memoryUpdateRequest : memoryUpdateRequests) {
            MemoryResponse memoryResponse = updatedPost.getMemories().stream()
                    .filter(memory -> memory.getId().equals(memoryUpdateRequest.getId()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(memoryUpdateRequest.getCaption(), memoryResponse.getCaption());
            assertEquals(memoryUpdateRequest.getIndex(), memoryResponse.getIndex());
            assertEquals(memoryUpdateRequest.getContentUrl(), memoryResponse.getContentUrl());
        }
    }

    @Test
    @DisplayName("포스트에 속한 메모리들의 컨텐츠 URL은 수정되지 않는다")
    void updateMemories_ShouldNotUpdateAnyUrls() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;

        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest createRequest = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getRequestId(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, createRequest).getHeaders().getLocation().getPath();
        PostResponse postBeforeUpdate = testRestTemplateUtils.requestGetPost(path);

        List<MemoryUpdateRequest> memoryUpdateRequests = postBeforeUpdate.getMemories()
                .stream()
                .map(memory -> new MemoryUpdateRequest(
                        memory.getId(),
                        memory.getIndex() + 1,
                        "수정됨" + memory.getCaption(),
                        "URL 수정 시도"))
                .toList();

        testRestTemplateUtils.requestUpdateMemories(postBeforeUpdate.getId(), memoryUpdateRequests);
        PostResponse updatedPost = testRestTemplateUtils.requestGetPost(path);

        assertNotNull(updatedPost);
        assertEquals(postBeforeUpdate.getId(), updatedPost.getId());
        assertEquals(memoryUpdateRequests.size(), updatedPost.getMemories().size());
        for (MemoryResponse memoryBeforeUpdate : postBeforeUpdate.getMemories()) {
            MemoryResponse memoryAfterUpdate = updatedPost.getMemories().stream()
                    .filter(memory -> memory.getId().equals(memoryBeforeUpdate.getId()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(memoryBeforeUpdate.getContentUrl(), memoryAfterUpdate.getContentUrl());
        }
    }

    @Test
    @DisplayName("포스트 메모리를 수정하면서 새로운 메모리를 추가할 수 있다")
    void updatePostMemories_canAddNewMemoires() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;

        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest createRequest = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getRequestId(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, createRequest).getHeaders().getLocation().getPath();
        PostResponse postBeforeUpdate = testRestTemplateUtils.requestGetPost(path);

        List<MemoryUpdateRequest> memoryUpdateRequests = postBeforeUpdate.getMemories()
                .stream()
                .map(memory -> new MemoryUpdateRequest(
                        memory.getId(),
                        memory.getIndex(),
                        memory.getCaption(),
                        memory.getContentUrl()
                ))
                .collect(Collectors.toList());
        memoryUpdateRequests.add(new MemoryUpdateRequest(null, -1.0, "추가된 메모리", "URL"));

        testRestTemplateUtils.requestUpdateMemories(postBeforeUpdate.getId(), memoryUpdateRequests);
        PostResponse updatedPost = testRestTemplateUtils.requestGetPost(path);

        assertNotNull(updatedPost);
        assertEquals(memoryUpdateRequests.size(), updatedPost.getMemories().size());
    }

    @Test
    @DisplayName("포스트 메모리 업데이트 요청에서 누락된 메모리는 삭제된다")
    void updatePostMemories_delete() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;

        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest createRequest = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getRequestId(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, createRequest).getHeaders().getLocation().getPath();
        PostResponse postBeforeUpdate = testRestTemplateUtils.requestGetPost(path);

        List<MemoryUpdateRequest> memoryUpdateRequests = postBeforeUpdate.getMemories()
                .stream()
                .map(memory -> new MemoryUpdateRequest(
                        memory.getId(),
                        memory.getIndex(),
                        memory.getCaption(),
                        memory.getContentUrl()
                ))
                .collect(Collectors.toList());
        memoryUpdateRequests.remove(0);

        testRestTemplateUtils.requestUpdateMemories(postBeforeUpdate.getId(), memoryUpdateRequests);
        PostResponse updatedPost = testRestTemplateUtils.requestGetPost(path);

        assertNotNull(updatedPost);
        assertEquals(memoryUpdateRequests.size(), updatedPost.getMemories().size());
    }

    @Test
    @DisplayName("커버 메모리 삭제 / 포스트 메모리 수정 / 첫번째 순서의 메모리의 URL이 포스트의 썸네일로 지정된다")
    void whenAddMemories_thenSuccess() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;

        List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(5);
        PostCreateRequest createRequest = new PostCreateRequest(
                seriesOneId,
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 7, 1),
                memoryRequestDtos.get(0).getRequestId(),
                memoryRequestDtos
        );
        String path = testRestTemplateUtils.requestCreatePost(albumId, createRequest).getHeaders().getLocation().getPath();
        PostResponse post = testRestTemplateUtils.requestGetPost(path);

        MemoryUpdateRequest first = new MemoryUpdateRequest(null, 0.0, "추가된 메모리", "썸네일이 될 URL");
        MemoryUpdateRequest second = new MemoryUpdateRequest(post.getMemories().get(4).getId(), 1.0, "수정된 메모리", post.getMemories().get(4).getContentUrl());
        List<MemoryUpdateRequest> memoryUpdateRequests = List.of(first, second);

        testRestTemplateUtils.requestUpdateMemories(post.getId(), memoryUpdateRequests);
        PostResponse updatedPost = testRestTemplateUtils.requestGetPost(path);

        assertNotNull(updatedPost);
        assertEquals(memoryUpdateRequests.size(), updatedPost.getMemories().size());
        assertEquals(first.getContentUrl(), updatedPost.getThumbnailUrl());
        assertEquals(first.getIndex(), updatedPost.getMemories().get(0).getIndex());
        assertEquals(first.getCaption(), updatedPost.getMemories().get(0).getCaption());
        assertEquals(first.getContentUrl(), updatedPost.getMemories().get(0).getContentUrl());
        assertEquals(second.getIndex(), updatedPost.getMemories().get(1).getIndex());
        assertEquals(second.getCaption(), updatedPost.getMemories().get(1).getCaption());
        assertEquals(second.getContentUrl(), updatedPost.getMemories().get(1).getContentUrl());
    }

    @Test
    @DisplayName("쿼리 파라미터 없음 / 포스트 목록 조회 / 가장 최근에 생성한 포스트 20개 반환")
    void successGetPostList() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;
        int postSize = 40;
        int defaultSize = 20;
        List<PostCreateRequest> postCreateRequests = new ArrayList<>();
        for (int i = 0; i < postSize; i++) {
            List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(1);
            PostCreateRequest createRequest = new PostCreateRequest(
                    seriesOneId,
                    String.format("포스트 %d", i),
                    String.format("포스트 소개문 %d", i),
                    LocalDate.of(2024, 7, 1).plusDays(i),
                    memoryRequestDtos.get(0).getRequestId(),
                    memoryRequestDtos
            );
            testRestTemplateUtils.requestCreatePost(albumId, createRequest);
            postCreateRequests.add(createRequest);
        }

//        //when
//        PostSearchResponse result = testRestTemplateUtils.requestGetPostList(albumId, null);
//
//        assertNotNull(result);
//        assertEquals(postSize, result.getTotal());
//        assertEquals(defaultSize, result.getContent().size());
//        for (int i = 0; i < result.getContent().size(); i++) {
//            PostCreateRequest request = postCreateRequests.get(postCreateRequests.size() - 1 - i);
//            PostListDto response = result.getContent().get(i);
//            assertEquals(request.getTitle(), response.getTitle());
//            assertEquals(request.getDescription(), response.getDescription());
//            assertEquals(request.getSeriesId(), response.getSeries().getId());
//            assertEquals(request.getMemoryDate(), response.getMemoryDate());
//        }
    }

    @Test
    @DisplayName("page = 0, size = 10 / 포스트 목록 조회 / 가장 최근에 생성한 포스트 10개 반환")
    void givenPage0AndSize10_whenGetPostList_thenReturn10Post() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;
        int postSize = 40;
        int page = 0;
        int size = 10;
        List<PostCreateRequest> postCreateRequests = new ArrayList<>();
        for (int i = 0; i < postSize; i++) {
            List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(1);
            PostCreateRequest createRequest = new PostCreateRequest(
                    seriesOneId,
                    String.format("포스트 %d", i),
                    String.format("포스트 소개문 %d", i),
                    LocalDate.of(2024, 7, 1).plusDays(i),
                    memoryRequestDtos.get(0).getRequestId(),
                    memoryRequestDtos
            );
            testRestTemplateUtils.requestCreatePost(albumId, createRequest);
            postCreateRequests.add(createRequest);
        }

        //when
//        PostSearchResponse result =
//                testRestTemplateUtils.requestGetPostList(albumId, String.format("?page=%d&size=%d", page, size));
//
//        assertNotNull(result);
//        assertEquals(postSize, result.getTotal());
//        assertEquals(size, result.getContent().size());
//        for (int i = 0; i < result.getContent().size(); i++) {
//            PostCreateRequest request = postCreateRequests.get(postCreateRequests.size() - 1 - i);
//            PostListDto response = result.getContent().get(i);
//            assertEquals(request.getTitle(), response.getTitle());
//            assertEquals(request.getDescription(), response.getDescription());
//            assertEquals(request.getSeriesId(), response.getSeries().getId());
//            assertEquals(request.getMemoryDate(), response.getMemoryDate());
//        }
    }

    @Test
    @DisplayName("page = 1, size = 10 / 포스트 목록 조회 / 두번째 페이지 포스트 10개 반환")
    void givenPage1AndSize10_whenGetPostList_thenReturn10Post() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;
        int postSize = 40;
        int page = 1;
        int size = 10;
        List<PostCreateRequest> postCreateRequests = new ArrayList<>();
        for (int i = 0; i < postSize; i++) {
            List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(1);
            PostCreateRequest createRequest = new PostCreateRequest(
                    seriesOneId,
                    String.format("포스트 %d", i),
                    String.format("포스트 소개문 %d", i),
                    LocalDate.of(2024, 7, 1).plusDays(i),
                    memoryRequestDtos.get(0).getRequestId(),
                    memoryRequestDtos
            );
            testRestTemplateUtils.requestCreatePost(albumId, createRequest);
            postCreateRequests.add(createRequest);
        }

        //when
//        PostSearchResponse result =
//                testRestTemplateUtils.requestGetPostList(albumId, String.format("?page=%d&size=%d", page, size));
//
//        assertNotNull(result);
//        assertEquals(postSize, result.getTotal());
//        assertEquals(size, result.getContent().size());
//        for (int i = 0; i < result.getContent().size(); i++) {
//            PostCreateRequest request = postCreateRequests.get(postCreateRequests.size() - size - i - 1);
//            PostListDto response = result.getContent().get(i);
//            assertEquals(request.getTitle(), response.getTitle());
//            assertEquals(request.getDescription(), response.getDescription());
//            assertEquals(request.getSeriesId(), response.getSeries().getId());
//            assertEquals(request.getMemoryDate(), response.getMemoryDate());
//        }
    }

    @Test
    @DisplayName("seriesId=1&sort=memoryDate,asc / 포스트 목록 조회 / 시리즈의 1 포스트 20개 기억 일시 오름차순 반환")
    void givenSeriesId_whenGetPostList_thenReturn10Post() {
        //given
        Long albumId = 1L;
        Long seriesOneId = 1L;
        Long seriesTowId = 2L;
        int postSize = 20;
        List<PostCreateRequest> postCreateRequests = new ArrayList<>();
        for (int i = 0; i < postSize; i++) {
            List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(1);
            PostCreateRequest createRequest = new PostCreateRequest(
                    seriesOneId,
                    String.format("시리즈 1 포스트 %d", i),
                    String.format("시리즈 1 포스트 소개문 %d", i),
                    LocalDate.of(2024, 7, 1).plusDays(i),
                    memoryRequestDtos.get(0).getRequestId(),
                    memoryRequestDtos
            );
            testRestTemplateUtils.requestCreatePost(albumId, createRequest);
            postCreateRequests.add(createRequest);
        }
        for (int i = 0; i < postSize; i++) {
            List<MemoryCreateRequest> memoryRequestDtos = testRestTemplateUtils.createMemoryRequestDto(1);
            PostCreateRequest createRequest = new PostCreateRequest(
                    seriesTowId,
                    String.format("시리즈 2 포스트 %d", i),
                    String.format("시리즈 2 소개문 %d", i),
                    LocalDate.of(2024, 7, 1).plusDays(i),
                    memoryRequestDtos.get(0).getRequestId(),
                    memoryRequestDtos
            );
            testRestTemplateUtils.requestCreatePost(albumId, createRequest);
        }

        //when
        RestPageImpl<PostListResponse> result =
                testRestTemplateUtils.requestGetPostList(albumId, "?seriesId=1&sort=memoryDate,asc");

//        assertNotNull(result);
//        assertEquals(postSize, result.getTotal());
//        assertEquals(postSize, result.getContent().size());
//        for (int i = 0; i < result.getContent().size(); i++) {
//            PostCreateRequest request = postCreateRequests.get(i);
//            PostListDto response = result.getContent().get(i);
//            assertEquals(request.getTitle(), response.getTitle());
//            assertEquals(request.getDescription(), response.getDescription());
//            assertEquals(request.getSeriesId(), response.getSeries().getId());
//            assertEquals(request.getMemoryDate(), response.getMemoryDate());
//        }
    }
}
