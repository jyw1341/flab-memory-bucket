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

import static com.zephyr.api.constant.TestConstant.*;
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
}
