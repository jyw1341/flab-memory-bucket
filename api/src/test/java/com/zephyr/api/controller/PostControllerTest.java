package com.zephyr.api.controller;

import com.zephyr.api.request.MemoryCreate;
import com.zephyr.api.request.PostCreate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.zephyr.api.utils.HttpRequestUtils.createUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("포스트 생성 요청 성공")
    public void successPostCreateRequest() {
        //given
        MemoryCreate memoryCreate1 = new MemoryCreate("Caption 1", "Content URL 1", "Location Name 1", "Location URL 1");
        MemoryCreate memoryCreate2 = new MemoryCreate("Caption 2", "Content URL 2", "Location Name 2", "Location URL 2");
        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
        PostCreate postCreate = new PostCreate(1L, 1L, "Test Title", "Test Description", LocalDate.now(), "Thumbnail URL", memoryCreateList);

        //when
        ResponseEntity<Void> response = restTemplate.postForEntity(
                createUrl(port, "/posts"),
                postCreate,
                Void.class
        );

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation().getPath());
    }
}
