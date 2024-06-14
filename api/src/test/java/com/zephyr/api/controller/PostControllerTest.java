package com.zephyr.api.controller;

import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.request.*;
import com.zephyr.api.dto.response.AlbumResponse;
import com.zephyr.api.dto.response.MemoryResponse;
import com.zephyr.api.dto.response.PostResponse;
import com.zephyr.api.dto.response.SeriesResponse;
import com.zephyr.api.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.zephyr.api.constant.TestConstant.*;
import static com.zephyr.api.utils.TestRequestUtils.createUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    private Member createMember() {
        Member member = Member.builder()
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .profileImageUrl(TEST_PROFILE_URL)
                .build();
        memberRepository.save(member);

        return member;
    }

    private AlbumResponse createAlbum() {
        AlbumCreateRequest albumCreateRequest = new AlbumCreateRequest(
                TEST_ALBUM_TITLE,
                TEST_ALBUM_DESC,
                TEST_ALBUM_THUMBNAIL
        );

        return restTemplate.postForEntity(
                createUrl(port, "/albums"),
                albumCreateRequest,
                AlbumResponse.class
        ).getBody();
    }

    private SeriesResponse createSeries(Long albumId) {
        SeriesCreateRequest seriesCreateRequest = new SeriesCreateRequest(
                albumId,
                TEST_SERIES_NAME
        );

        return restTemplate.postForEntity(
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
        createMember();
        AlbumResponse albumResponse = createAlbum();
        SeriesResponse seriesResponse = createSeries(albumResponse.getId());
        List<MemoryCreateRequest> memoryCreateRequests = makeMemoryRequests(5);
        int thumbnailMemoryIndex = 0;
        PostCreateRequest postCreateRequest = makePostCreateRequest(
                albumResponse.getId(),
                seriesResponse.getId(),
                thumbnailMemoryIndex,
                memoryCreateRequests
        );

        //when
        ResponseEntity<PostResponse> responseEntity = createPost(postCreateRequest);
        PostResponse postResponse = responseEntity.getBody();

        //then
        //포스트 내용 비교
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(postResponse.getId());
        assertEquals(postCreateRequest.getTitle(), postResponse.getTitle());
        assertEquals(postCreateRequest.getDescription(), postResponse.getDescription());
        assertEquals(postCreateRequest.getMemoryDate(), postResponse.getMemoryDate());
        assertEquals(seriesResponse.getName(), postResponse.getSeries());
        assertEquals(postCreateRequest.getMemoryCreateRequests().get(thumbnailMemoryIndex).getContentUrl(), postResponse.getThumbnailUrl());
        assertNotNull(postResponse.getCreatedAt());

        //메모리 비교
        assertEquals(postCreateRequest.getMemoryCreateRequests().size(), postResponse.getMemories().size());
        for (MemoryCreateRequest createRequest : postCreateRequest.getMemoryCreateRequests()) {
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
        createMember();
        AlbumResponse albumResponse = createAlbum();
        SeriesResponse seriesResponse = createSeries(albumResponse.getId());
        List<MemoryCreateRequest> memoryCreateRequests = makeMemoryRequests(5);
        int thumbnailMemoryIndex = 0;
        PostCreateRequest postCreateRequest = makePostCreateRequest(
                albumResponse.getId(),
                seriesResponse.getId(),
                thumbnailMemoryIndex,
                memoryCreateRequests
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
