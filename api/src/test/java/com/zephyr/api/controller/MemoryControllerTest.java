package com.zephyr.api.controller;

import com.zephyr.api.request.ContentCreate;
import com.zephyr.api.request.MemoryCreate;
import com.zephyr.api.response.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static com.zephyr.api.utils.HttpRequestUtils.createUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemoryControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("앨범 아이디 null / 기억 생성 / 400 반환")
    void givenNullAlbumId_whenCreateMemory_thenReturn400() {
        List<ContentCreate> contentCreates = new ArrayList<>();
        ContentCreate contentCreate = new ContentCreate("제목", "설명", "url", 1);
        contentCreates.add(contentCreate);
        MemoryCreate request = new MemoryCreate(
                null,
                "제목",
                "설명",
                "2024-05-30",
                new ArrayList<>(),
                contentCreates
        );

        //when
        ResponseEntity<ErrorResponse> result = restTemplate.postForEntity(
                createUrl(port, "/memories"),
                request,
                ErrorResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().getValidation().containsKey("albumId"));
    }

    @Test
    @DisplayName("기억 제목이 최대값 초과 / 기억 생성 / 400 반환")
    void givenOverMaxTitleLength_whenCreateMemory_thenReturn400() {
        List<ContentCreate> contentCreates = new ArrayList<>();
        ContentCreate contentCreate = new ContentCreate("제목", "설명", "url", 1);
        contentCreates.add(contentCreate);
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() <= MemoryCreate.MEMORY_TITLE_MAX) {
            stringBuilder.append("a");
        }

        MemoryCreate request = new MemoryCreate(
                1L,
                stringBuilder.toString(),
                "설명",
                "2024-05-30",
                new ArrayList<>(),
                contentCreates
        );

        //when
        ResponseEntity<ErrorResponse> result = restTemplate.postForEntity(
                createUrl(port, "/memories"),
                request,
                ErrorResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().getValidation().containsKey("title"));
    }

    @Test
    @DisplayName("기억 제목이 빈 문자열 / 기억 생성 / 400 반환")
    void givenEmptyMemoryTitle_whenCreateMemory_thenReturn400() {
        List<ContentCreate> contentCreates = new ArrayList<>();
        ContentCreate contentCreate = new ContentCreate("제목", "설명", "url", 1);
        contentCreates.add(contentCreate);
        MemoryCreate request = new MemoryCreate(
                1L,
                "",
                "설명",
                "2024-05-30",
                new ArrayList<>(),
                contentCreates
        );

        //when
        ResponseEntity<ErrorResponse> result = restTemplate.postForEntity(
                createUrl(port, "/memories"),
                request,
                ErrorResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().getValidation().containsKey("title"));
    }

    @Test
    @DisplayName("기억 제목이 공백일 때 / 기억 생성 / 400 반환")
    void givenBlankMemoryTitle_whenCreateMemory_thenReturn400() {
        List<ContentCreate> contentCreates = new ArrayList<>();
        ContentCreate contentCreate = new ContentCreate("제목", "설명", "url", 1);
        contentCreates.add(contentCreate);
        MemoryCreate request = new MemoryCreate(
                1L,
                "  ",
                "설명",
                "2024-05-30",
                new ArrayList<>(),
                contentCreates
        );

        //when
        ResponseEntity<ErrorResponse> result = restTemplate.postForEntity(
                createUrl(port, "/memories"),
                request,
                ErrorResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().getValidation().containsKey("title"));
    }

    @Test
    @DisplayName("기억 제목이 null / 기억 생성 / 400 반환")
    void givenNullMemoryTitle_whenCreateMemory_thenReturn400() {
        List<ContentCreate> contentCreates = new ArrayList<>();
        ContentCreate contentCreate = new ContentCreate("제목", "설명", "url", 1);
        contentCreates.add(contentCreate);
        MemoryCreate request = new MemoryCreate(
                1L,
                null,
                "설명",
                "2024-05-30",
                new ArrayList<>(),
                contentCreates
        );

        //when
        ResponseEntity<ErrorResponse> result = restTemplate.postForEntity(
                createUrl(port, "/memories"),
                request,
                ErrorResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().getValidation().containsKey("title"));
    }

    @Test
    @DisplayName("기억 설명 텍스트 길이가 최대값 초과 / 기억 생성 / 400 반환")
    void givenOverMaxDescriptionLength_whenCreateMemory_thenReturn400() {
        List<ContentCreate> contentCreates = new ArrayList<>();
        ContentCreate contentCreate = new ContentCreate("제목", "설명", "url", 1);
        contentCreates.add(contentCreate);
        StringBuilder description = new StringBuilder();
        while (description.length() <= MemoryCreate.MEMORY_DESCRIPTION_MAX) {
            description.append("a");
        }

        MemoryCreate request = new MemoryCreate(
                1L,
                "제목",
                description.toString(),
                "2024-05-30",
                new ArrayList<>(),
                contentCreates
        );

        //when
        ResponseEntity<ErrorResponse> result = restTemplate.postForEntity(
                createUrl(port, "/memories"),
                request,
                ErrorResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().getValidation().containsKey("description"));
    }

    @Test
    @DisplayName("기억 일시 값이 없을 때 / 기억 생성 / 400 반환")
    void givenNullMemoryDate_whenCreateMemory_thenReturn400() {
        List<ContentCreate> contentCreates = new ArrayList<>();
        ContentCreate contentCreate = new ContentCreate("제목", "설명", "url", 1);
        contentCreates.add(contentCreate);
        MemoryCreate request = new MemoryCreate(
                1L,
                "제목",
                "설명",
                null,
                new ArrayList<>(),
                contentCreates
        );

        //when
        ResponseEntity<ErrorResponse> result = restTemplate.postForEntity(
                createUrl(port, "/memories"),
                request,
                ErrorResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().getValidation().containsKey("memoryDate"));
    }

    @Test
    @DisplayName("컨텐츠가 빈 리스트일 때 / 기억 생성 / 400 반환")
    void givenEmptyContents_whenCreateMemory_thenReturn400() {
        List<ContentCreate> contentCreates = new ArrayList<>();
        MemoryCreate request = new MemoryCreate(
                1L,
                "제목",
                "설명",
                "2024-05-30",
                new ArrayList<>(),
                contentCreates
        );

        //when
        ResponseEntity<ErrorResponse> result = restTemplate.postForEntity(
                createUrl(port, "/memories"),
                request,
                ErrorResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().getValidation().containsKey("contents"));
    }

    @Test
    @DisplayName("컨텐츠가 널일 때 / 기억 생성 / 400 반환")
    void givenNullContents_whenCreateMemory_thenReturn400() {
        MemoryCreate request = new MemoryCreate(
                1L,
                "제목",
                "설명",
                "2024-05-30",
                new ArrayList<>(),
                null
        );

        //when
        ResponseEntity<ErrorResponse> result = restTemplate.postForEntity(
                createUrl(port, "/memories"),
                request,
                ErrorResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().getValidation().containsKey("contents"));
    }
}
