package com.zephyr.api.controller;

import com.zephyr.api.config.TestConfig;
import com.zephyr.api.dto.request.AlbumCreateRequest;
import com.zephyr.api.dto.request.MemberCreateRequest;
import com.zephyr.api.dto.request.MemoryCreateRequest;
import com.zephyr.api.dto.request.SeriesCreateRequest;
import com.zephyr.api.dto.response.AlbumResponse;
import com.zephyr.api.dto.response.SeriesResponse;
import com.zephyr.api.utils.H2TableCleaner;
import com.zephyr.api.utils.TestRestTemplateUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.List;

import static com.zephyr.api.utils.TestConstant.*;
import static com.zephyr.api.utils.TestStringUtils.createUrl;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestConfig.class)
class SeriesControllerTest {

    private final H2TableCleaner h2TableCleaner;
    private final TestRestTemplate restTemplate;
    private final int port;
    private final TestRestTemplateUtils restTemplateUtils;
    private AlbumResponse album;

    public SeriesControllerTest(
            @Autowired TestRestTemplate restTemplate,
            @Autowired H2TableCleaner h2TableCleaner,
            @LocalServerPort int port) {
        this.restTemplate = restTemplate;
        this.port = port;
        this.h2TableCleaner = h2TableCleaner;
        restTemplateUtils = new TestRestTemplateUtils(restTemplate, port);
    }

    @BeforeAll
    void setUpAll() {
        restTemplateUtils.requestCreateMember(new MemberCreateRequest(
                "test@example.com",
                "username",
                "http://profile.url"));
        album = restTemplateUtils.requestCreateAlbum(new AlbumCreateRequest(
                TEST_ALBUM_TITLE,
                TEST_ALBUM_DESC,
                TEST_ALBUM_THUMBNAIL));
    }

    @AfterEach
    void cleanUp() {
        h2TableCleaner.clean("series");
    }

    @Test
    @DisplayName("")
    void test() {
        //given
        SeriesResponse series1 = restTemplateUtils.requestCreateSeries(new SeriesCreateRequest(
                album.getId(),
                "시리즈1"));
        SeriesResponse series2 = restTemplateUtils.requestCreateSeries(new SeriesCreateRequest(
                album.getId(),
                "시리즈2"));
        List<MemoryCreateRequest> memoryRequestDtos1 = restTemplateUtils.createMemoryRequestDto(1);


        //when
        restTemplate.exchange(
                createUrl(port, String.format("/albums/%d/series", album.getId())),
                HttpMethod.GET,
                new HttpEntity<>(null),
                Void.class);
    }
}
