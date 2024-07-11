package com.zephyr.api.controller;

import com.zephyr.api.dto.request.PresignedUrlCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.zephyr.api.utils.TestRestTemplateUtils.createUrl;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("")
    void successCreatePresignedUrl() {
        //given
        List<PresignedUrlCreateRequest> requests = createRequests();

        //when
        ResponseEntity<Map<Integer, String>> response = restTemplate.exchange(
                createUrl(port, "/files"),
                HttpMethod.POST,
                new HttpEntity<>(requests),
                new ParameterizedTypeReference<>() {
                }
        );
        Map<Integer, String> responseBody = response.getBody();

        //then

        assertNotNull(responseBody);
        assertEquals(requests.size(), responseBody.size());
        for (PresignedUrlCreateRequest request : requests) {
            assertTrue(responseBody.containsKey(request.getFileIndex()));
        }
    }

    private List<PresignedUrlCreateRequest> createRequests() {
        List<PresignedUrlCreateRequest> result = new ArrayList<>();

        result.add(new PresignedUrlCreateRequest(0, "test1.jpg", 4234L));
        result.add(new PresignedUrlCreateRequest(1, "test2.png", 1024L));
        result.add(new PresignedUrlCreateRequest(2, "test3.mp4", 132024L));

        return result;
    }
}
