package com.zephyr.api.controller;

import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.request.AlbumCreateRequest;
import com.zephyr.api.dto.response.AlbumResponse;
import com.zephyr.api.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

import static com.zephyr.api.constant.TestConstant.*;
import static com.zephyr.api.utils.HttpRequestUtils.createUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class AlbumControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    @Autowired
    private MemberRepository memberRepository;

    private void createMember() {
        Member member = Member.builder()
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .profileImageUrl(TEST_PROFILE_URL)
                .build();
        memberRepository.save(member);
    }

    @Test
    @DisplayName("앨범 생성 성공")
    void successCreateAlbum() {
        //given
        createMember();
        AlbumCreateRequest albumCreateRequest = new AlbumCreateRequest(
                TEST_ALBUM_TITLE,
                TEST_ALBUM_DESC,
                TEST_ALBUM_THUMBNAIL
        );

        //when
        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(port, "/albums"),
                albumCreateRequest,
                AlbumResponse.class
        );
        AlbumResponse result = response.getBody();

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertEquals(albumCreateRequest.getTitle(), result.getTitle());
        assertEquals(albumCreateRequest.getDescription(), result.getDescription());
        assertEquals(albumCreateRequest.getThumbnailUrl(), result.getThumbnailUrl());

    }

    @Test
    @DisplayName("필수 필드만 있는 생성 요청 / 앨범 생성 / 201, 필수 필드만 있는 응답 반환")
    void givenValidRequest_whenCreateAlbum_thenStatus201AndOptionalFieldIsNull() throws Exception {
        //given
        AlbumCreateRequest request = new AlbumCreateRequest("테스트 앨범", "test title", null);

        //when
        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(port, "/albums"),
                request,
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(request.getTitle(), response.getBody().getTitle());
        //TODO : 엔티티 개발 후 추가
    }

    @Test
    @DisplayName("필수 필드가 없는 생성 요청 / 앨범 생성 / 400 반환")
    public void givenMissingAlbumName_whenCreateAlbum_thenBadRequest() throws Exception {
        //given
        AlbumCreateRequest request = new AlbumCreateRequest("테스트 앨범", "test title", null);

        //when
        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(port, "/albums"),
                request,
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("앨범 생성 / 앨범 단건 조회 / 200, 앨범 정보 반환")
    void givenValidAlbumId_whenGetAlbum_thenStatus200() {
        //given
        AlbumCreateRequest request = new AlbumCreateRequest("테스트 앨범", null, null);
        ResponseEntity<AlbumResponse> responseEntity = restTemplate.postForEntity(
                createUrl(port, "/albums"),
                request,
                AlbumResponse.class
        );
        URI location = responseEntity.getHeaders().getLocation();

        //when
        ResponseEntity<AlbumResponse> resultEntity = restTemplate.getForEntity(
                createUrl(port, location.getPath()),
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.OK, resultEntity.getStatusCode());
        assertEquals(request.getTitle(), resultEntity.getHeaders().getLocation());
        //TODO : 엔티티 개발 후 추가
    }

    @Test
    @DisplayName("앨범 생성 x / 앨범 단건 조회 / 404 반환")
    void givenInvalidAlbumId_whenGetAlbum_thenStatus404() throws Exception {
        //given
        String invalidPath = "albums/999";

        //when
        ResponseEntity<AlbumResponse> resultEntity = restTemplate.getForEntity(
                createUrl(port, invalidPath),
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
            AlbumCreateRequest request = new AlbumCreateRequest("테스트 앨범" + i, null, null);
            restTemplate.postForEntity(
                    createUrl(port, "/albums"),
                    request,
                    AlbumResponse.class
            );
        }

        //when
        ResponseEntity<AlbumResponse[]> resultEntity = restTemplate.getForEntity(
                createUrl(port, "/albums"),
                AlbumResponse[].class
        );

        //then
        assertEquals(HttpStatus.OK, resultEntity.getStatusCode());
        assertEquals(resultSize, resultEntity.getBody().length);
    }

    @Test
    @DisplayName("앨범 생성 / 앨범 삭제 / 204, 생성된 앨범 삭제")
    public void givenValidAlbumId_whenDeleteAlbum_thenAlbumIsDeleted() throws Exception {
        //given
        AlbumCreateRequest request = new AlbumCreateRequest("테스트 앨범", "test title", null);

        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(port, "/albums"),
                request,
                AlbumResponse.class
        );
        URI location = response.getHeaders().getLocation();

        //when
        ResponseEntity<Void> result = restTemplate.exchange(
                createUrl(port, location.getPath()),
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
                createUrl(port, invalidPath),
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
        AlbumCreateRequest request = new AlbumCreateRequest("테스트 앨범", "test title", null);
        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(port, "/albums"),
                request,
                AlbumResponse.class
        );
        URI location = response.getHeaders().getLocation();

        //when
        AlbumCreateRequest updateRequest = new AlbumCreateRequest("테스트 앨범", "test title", null);
        ResponseEntity<AlbumResponse> result = restTemplate.exchange(
                createUrl(port, location.getPath()),
                HttpMethod.PATCH,
                new HttpEntity<>(updateRequest),
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(updateRequest.getTitle(), result.getBody().getTitle());
        //TODO : 엔티티 개발 후 추가
    }

    @Test
    @DisplayName("없는 앨범 ID / 앨범 수정 / 404")
    public void givenInvalidAlbumId_whenUpdateAlbum_thenStatus404() throws Exception {
        //given
        String invalidPath = "albums/999";

        //when
        AlbumCreateRequest request = new AlbumCreateRequest("테스트 앨범", "test title", null);
        ResponseEntity<AlbumResponse> result = restTemplate.exchange(
                createUrl(port, invalidPath),
                HttpMethod.PATCH,
                new HttpEntity<>(request),
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    @DisplayName("필수 필드가 없는 수정 요청 / 앨범 수정 / 400")
    public void givenRequestBodyWithMissingData_whenUpdateAlbum_thenStatus400() throws Exception {
        //given
        AlbumCreateRequest request = new AlbumCreateRequest("테스트 앨범", "test title", null);
        ResponseEntity<AlbumResponse> response = restTemplate.postForEntity(
                createUrl(port, "/albums"),
                request,
                AlbumResponse.class
        );
        URI location = response.getHeaders().getLocation();

        //when
        AlbumCreateRequest invalidUpdateRequest = new AlbumCreateRequest(null, null, null);
        ResponseEntity<AlbumResponse> result = restTemplate.exchange(
                createUrl(port, location.getPath()),
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
