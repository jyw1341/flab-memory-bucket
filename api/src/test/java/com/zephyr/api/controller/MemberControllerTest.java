package com.zephyr.api.controller;

import com.zephyr.api.dto.request.MemberCreateRequest;
import com.zephyr.api.dto.response.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static com.zephyr.api.utils.TestStringUtils.createUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("멤버 생성 성공")
    void successCreateMember() {
        //given
        MemberCreateRequest request = new MemberCreateRequest(
                "test@example.com",
                "username",
                "http://profile.url");

        //when
        ResponseEntity<MemberResponse> response = restTemplate.postForEntity(
                createUrl(port, "/members"),
                request,
                MemberResponse.class
        );

        //then
        assertEquals(request.getUsername(), response.getBody().getUsername());

    }

}
