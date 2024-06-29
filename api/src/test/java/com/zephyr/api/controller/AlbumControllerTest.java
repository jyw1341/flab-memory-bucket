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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.zephyr.api.utils.TestConstant.*;
import static com.zephyr.api.utils.TestStringUtils.createUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
                .profileUrl(TEST_PROFILE_URL)
                .build();
        memberRepository.save(member);
    }

    private AlbumCreateRequest createRequest() {
        return new AlbumCreateRequest(
                TEST_ALBUM_TITLE,
                TEST_ALBUM_DESC,
                TEST_ALBUM_THUMBNAIL
        );
    }


    @Test
    @DisplayName("앨범 생성 성공")
    void successCreateAlbum() {
        //given
        createMember();
        AlbumCreateRequest albumCreateRequest = createRequest();

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
    @DisplayName("앨범 목록 조회 성공")
    void successGetAlbumList() {
        //given
        createMember();

        for (int i = 0; i < 20; i++) {
            restTemplate.postForEntity(
                    createUrl(port, "/albums"),
                    createRequest(),
                    AlbumResponse.class
            );
        }

        //when
        ResponseEntity<Object> result = restTemplate.getForEntity(
                createUrl(port, "/albums?page=1&size=10"),
                Object.class
        );

        //then
    }
}
