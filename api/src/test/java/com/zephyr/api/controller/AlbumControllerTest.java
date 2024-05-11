package com.zephyr.api.controller;

import com.zephyr.api.request.AlbumCreate;
import com.zephyr.api.request.AlbumUpdate;
import com.zephyr.api.response.AlbumResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AlbumControllerTest {

    public static final String LOCALHOST = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private static String createUrl(String baseUrl, int port, String path) {
        return baseUrl + port + path;
    }

    @Test
    @DisplayName("앨범 생성 / 앨범 단건 조회 / 200, 앨범 정보 반환")
    void givenValidAlbumId_whenGetAlbum_thenStatus200() throws Exception {
        //given
        AlbumCreate request = AlbumCreate.builder()
                .albumTitle("테스트 앨범")
                .build();
        ResponseEntity<AlbumResponse> responseEntity = restTemplate.postForEntity(
                createUrl(LOCALHOST, port, "/albums"),
                request,
                AlbumResponse.class
        );
        URI location = responseEntity.getHeaders().getLocation();

        //when
        ResponseEntity<AlbumResponse> resultEntity = restTemplate.getForEntity(
                createUrl(LOCALHOST, port, location.getPath()),
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.OK, resultEntity.getStatusCode());
        assertEquals(request.getAlbumTitle(), resultEntity.getBody().getAlbumTitle());
        //TODO : 엔티티 개발 후 추가
    }

    @Test
    @DisplayName("앨범 생성 x / 앨범 단건 조회 / 404 반환")
    void givenInvalidAlbumId_whenGetAlbum_thenStatus404() throws Exception {
        //given
        String invalidPath = "albums/999";

        //when
        ResponseEntity<AlbumResponse> resultEntity = restTemplate.getForEntity(
                createUrl(LOCALHOST, port, invalidPath),
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.NOT_FOUND, resultEntity.getStatusCode());
    }

    @Test
    @DisplayName("앨범 10개 생성 / 앨범 목록 조회 / 200, 앨범 10개 반환")
    public void givenValidRequest_whenGetList_thenStatus200AndCorrectAlbums() throws Exception {
        //given
        int resultSize = 10;
        for (int i = 0; i < resultSize; i++) {
            AlbumCreate request = AlbumCreate.builder().albumTitle("테스트 앨범" + i).build();
            restTemplate.postForEntity(
                    createUrl(LOCALHOST, port, "/albums"),
                    request,
                    AlbumResponse.class
            );
        }

        //when
        ResponseEntity<AlbumResponse[]> resultEntity = restTemplate.getForEntity(
                createUrl(LOCALHOST, port, "/albums"),
                AlbumResponse[].class
        );

        //then
        assertEquals(HttpStatus.OK, resultEntity.getStatusCode());
        assertEquals(resultSize, resultEntity.getBody().length);
    }

    @Test
    @DisplayName("모든 필드가 있는 앨범 생성 요청 / 앨범 생성 / 201, 모든 필드 정보가 있는 응답 반환")
    void givenValidRequest_whenCreateAlbum_thenStatus201AndCorrectResponse() throws Exception {
        //given
        AlbumCreate request = AlbumCreate.builder()
                .ownerId("tester")
                .albumTitle("test title")
                .albumDescription("hello")
                .build();

        //when
        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(LOCALHOST, port, "/albums"),
                request,
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(request.getAlbumTitle(), response.getBody().getAlbumTitle());
        //TODO : 엔티티 개발 후 추가

    }

    @Test
    @DisplayName("필수 필드만 있는 생성 요청 / 앨범 생성 / 201, 필수 필드만 있는 응답 반환")
    void givenValidRequest_whenCreateAlbum_thenStatus201AndOptionalFieldIsNull() throws Exception {
        //given
        AlbumCreate request = AlbumCreate.builder()
                .ownerId("tester")
                .albumTitle("test title")
                .build();

        //when
        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(LOCALHOST, port, "/albums"),
                request,
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(request.getAlbumTitle(), response.getBody().getAlbumTitle());
        //TODO : 엔티티 개발 후 추가
    }

    @Test
    @DisplayName("필수 필드가 없는 생성 요청 / 앨범 생성 / 400 반환")
    public void givenMissingAlbumName_whenCreateAlbum_thenBadRequest() throws Exception {
        //given
        AlbumCreate request = AlbumCreate.builder()
                .albumDescription("hello")
                .build();

        //when
        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(LOCALHOST, port, "/albums"),
                request,
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("앨범 생성 / 앨범 삭제 / 204, 생성된 앨범 삭제")
    public void givenValidAlbumId_whenDeleteAlbum_thenAlbumIsDeleted() throws Exception {
        //given
        AlbumCreate request = AlbumCreate.builder()
                .ownerId("tester")
                .albumTitle("test title")
                .albumDescription("hello")
                .build();
        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(LOCALHOST, port, "/albums"),
                request,
                AlbumResponse.class
        );
        URI location = response.getHeaders().getLocation();

        //when
        ResponseEntity<Void> result = restTemplate.exchange(
                createUrl(LOCALHOST, port, location.getPath()),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );
        //TODO 실제 조회?

        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    @DisplayName("없는 앨범 ID / 앨범 삭제 / 404 반환")
    public void givenInvalidAlbumId_whenDeleteAlbum_thenNotFound() throws Exception {
        //given
        String invalidPath = "albums/999";

        //when
        ResponseEntity<Void> result = restTemplate.exchange(
                createUrl(LOCALHOST, port, invalidPath),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    @DisplayName("앨범 생성 / 생성한 앨범 수정 / 200, 변경된 앨범 정보 반환")
    public void givenValidAlbumIdAndValidRequestBody_whenUpdateAlbum_thenStatus200AndCorrectResponse() throws Exception {
        //given
        AlbumCreate request = AlbumCreate.builder()
                .ownerId("tester")
                .albumTitle("test title")
                .albumDescription("hello")
                .build();
        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(LOCALHOST, port, "/albums"),
                request,
                AlbumResponse.class
        );
        URI location = response.getHeaders().getLocation();

        //when
        AlbumUpdate updateRequest = AlbumUpdate.builder()
                .ownerId("updated")
                .albumTitle("updated title")
                .albumDescription("updated hello")
                .build();
        ResponseEntity<AlbumResponse> result = restTemplate.exchange(
                createUrl(LOCALHOST, port, location.getPath()),
                HttpMethod.PATCH,
                new HttpEntity<>(updateRequest),
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(updateRequest.getAlbumTitle(), result.getBody().getAlbumTitle());
        //TODO : 엔티티 개발 후 추가
    }

    @Test
    @DisplayName("없는 앨범 ID / 앨범 수정 / 404")
    public void givenInvalidAlbumId_whenUpdateAlbum_thenStatus404() throws Exception {
        //given
        String invalidPath = "albums/999";

        //when
        AlbumUpdate updateRequest = AlbumUpdate.builder()
                .ownerId("updated")
                .albumTitle("updated title")
                .albumDescription("updated hello")
                .build();
        ResponseEntity<AlbumResponse> result = restTemplate.exchange(
                createUrl(LOCALHOST, port, invalidPath),
                HttpMethod.PATCH,
                new HttpEntity<>(updateRequest),
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    @DisplayName("필수 필드가 없는 수정 요청 / 앨범 수정 / 400")
    public void givenRequestBodyWithMissingData_whenUpdateAlbum_thenStatus400() throws Exception {
        //given
        AlbumCreate request = AlbumCreate.builder()
                .ownerId("tester")
                .albumTitle("test title")
                .albumDescription("hello")
                .build();
        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(LOCALHOST, port, "/albums"),
                request,
                AlbumResponse.class
        );
        URI location = response.getHeaders().getLocation();

        //when
        AlbumUpdate invalidUpdateRequest = AlbumUpdate.builder()
                .ownerId("updated")
                .albumDescription("updated hello")
                .build();
        ResponseEntity<AlbumResponse> result = restTemplate.exchange(
                createUrl(LOCALHOST, port, location.getPath()),
                HttpMethod.PATCH,
                new HttpEntity<>(invalidUpdateRequest),
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    @DisplayName("앨범 멤버 추가 / 앨범 멤버 목록 조회 / 200, 멤버 목록 반환")
    public void givenValidAlbumId_whenGetMembers_thenStatus200AndCorrectMembers() throws Exception {

    }

    @Test
    @DisplayName("없는 앨범 ID 사용 / 앨범 멤버 목록 조회 / 404 반환")
    public void givenInvalidAlbumId_whenGetMembers_thenStatus404() throws Exception {

    }

    @Test
    @DisplayName("유효 앨범 ID, 유효 멤버 ID / 앨범 멤버 삭제 / 204 반환, 멤버 목록에서 삭제 확인")
    public void givenValidAlbumIdAndValidMemberId_whenDeleteMember_thenStatus204() throws Exception {

    }

    @Test
    @DisplayName("없는 앨범 ID, 유효 멤버 ID / 앨범 멤버 삭제 / 404 반환")
    public void givenInvalidAlbumId_whenDeleteMember_thenStatus404() throws Exception {

    }

    @Test
    @DisplayName("유효 앨범 ID, 없는 멤버 ID / 앨범 멤버 삭제 / 404 반환")
    public void givenValidAlbumIdAndInvalidMemberId_whenDeleteMember_thenStatus404() throws Exception {

    }
}
