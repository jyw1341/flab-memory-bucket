package com.zephyr.api.utils;

import com.zephyr.api.dto.request.*;
import com.zephyr.api.dto.response.*;
import com.zephyr.api.enums.ContentType;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestRestTemplateUtils {

    private final TestRestTemplate restTemplate;
    private final int port;

    public TestRestTemplateUtils(TestRestTemplate restTemplate, int port) {
        this.restTemplate = restTemplate;
        this.port = port;
    }

    public static String createUrl(int port, String path) {
        return String.format("http://localhost:%d%s", port, path);
    }

    public static String createUrl(int port, String path, String params) {
        if (params == null) {
            params = "";
        }
        String format = String.format("http://localhost:%d%s%s", port, path, params);
        return format;
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

    public ResponseEntity<Void> requestCreateSeries(Long albumId, SeriesCreateRequest request) {
        return restTemplate.postForEntity(
                createUrl(port, String.format("/albums/%d/series", albumId)),
                request,
                Void.class
        );
    }

    public void requestCreateSeries(Long albumId, List<SeriesCreateRequest> requests) {
        for (SeriesCreateRequest request : requests) {
            restTemplate.postForEntity(
                    createUrl(port, String.format("/albums/%d/series", albumId)),
                    request,
                    Void.class
            );
        }
    }

    public SeriesResponse requestGetSeries(Long seriesId) {
        return restTemplate.getForEntity(
                createUrl(port, "/series/" + seriesId),
                SeriesResponse.class
        ).getBody();
    }

    public SeriesResponse requestGetSeries(String path) {
        return restTemplate.getForEntity(
                createUrl(port, path),
                SeriesResponse.class
        ).getBody();
    }

    public List<SeriesResponse> requestGetSeriesList(Long albumId) {
        return restTemplate.exchange(
                createUrl(port, String.format("/albums/%d/series", albumId)),
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<List<SeriesResponse>>() {
                }
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

    public RestPageImpl<PostListResponse> requestGetPostList(Long albumId, String params) {
        return restTemplate.exchange(
                createUrl(port, String.format("/albums/%d/posts", albumId), params),
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<RestPageImpl<PostListResponse>>() {
                }
        ).getBody();
    }

    public ResponseEntity<Void> requestUpdatePost(Long postId, PostUpdateRequest postUpdateRequest) {
        return restTemplate.postForEntity(
                createUrl(port, "/posts/" + postId),
                new HttpEntity<>(postUpdateRequest),
                Void.class
        );
    }

    public void requestUpdateMemories(Long postId, List<MemoryUpdateRequest> requests) {
        restTemplate.postForEntity(
                createUrl(port, "/posts/" + postId + "/memories"),
                requests,
                Void.class
        );
    }

    public List<SeriesCreateRequest> makeSeriesCreateRequest(int size) {
        List<SeriesCreateRequest> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(new SeriesCreateRequest("시리즈 " + i));
        }

        return result;
    }

    public List<MemoryCreateRequest> createMemoryRequestDto(int size) {
        List<MemoryCreateRequest> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MemoryCreateRequest request = new MemoryCreateRequest(
                    UUID.randomUUID().toString(),
                    ContentType.IMAGE,
                    (double) i,
                    "Content URL " + i,
                    "Caption " + i
            );
            result.add(request);
        }

        return result;
    }
}
