package com.zephyr.api.utils;

import com.zephyr.api.dto.request.*;
import com.zephyr.api.dto.response.AlbumResponse;
import com.zephyr.api.dto.response.MemberResponse;
import com.zephyr.api.dto.response.PostResponse;
import com.zephyr.api.dto.response.SeriesResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static com.zephyr.api.utils.TestStringUtils.createUrl;

public class TestRestTemplateUtils {

    private final TestRestTemplate restTemplate;
    private final int port;

    public TestRestTemplateUtils(TestRestTemplate restTemplate, int port) {
        this.restTemplate = restTemplate;
        this.port = port;
    }

    public void requestCreateMember(MemberCreateRequest request) {
        restTemplate.postForEntity(
                createUrl(port, "/members"),
                request,
                MemberResponse.class);
    }

    public AlbumResponse requestCreateAlbum(AlbumCreateRequest request) {
        return restTemplate.postForEntity(
                createUrl(port, "/albums"),
                request,
                AlbumResponse.class
        ).getBody();
    }

    public SeriesResponse requestCreateSeries(SeriesCreateRequest request) {
        return restTemplate.postForEntity(
                createUrl(port, "/albums/" + request.getAlbumId() + "/series"),
                request,
                SeriesResponse.class
        ).getBody();
    }

    public SeriesResponse requestGetSeries(Long seriesId) {
        return restTemplate.getForEntity(
                createUrl(port, "/series/" + seriesId),
                SeriesResponse.class
        ).getBody();
    }

    public ResponseEntity<Void> requestCreatePost(Long albumId, PostCreateRequest request) {
        return restTemplate.postForEntity(
                createUrl(port, String.format("/albums/%d/posts", albumId)),
                request,
                Void.class
        );
    }

    public PostResponse requestGetPost(Long postId) {
        return restTemplate.getForEntity(
                createUrl(port, "/posts/" + postId),
                PostResponse.class
        ).getBody();
    }

    public PostResponse requestGetPost(String path) {
        return restTemplate.getForEntity(
                createUrl(port, path),
                PostResponse.class
        ).getBody();
    }

    public void requestUpdatePost(Long postId, PostUpdateRequest postUpdateRequest) {
        restTemplate.patchForObject(
                createUrl(port, "/posts/" + postId),
                postUpdateRequest,
                Void.class
        );
    }

    public void requestUpdateMemories(Long postId, List<MemoryUpdateRequest> requests) {
        restTemplate.patchForObject(
                createUrl(port, "/posts/" + postId + "/memories"),
                requests,
                Void.class
        );
    }

    public List<MemoryCreateRequest> createMemoryRequestDto(int size) {
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
}
