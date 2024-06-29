package com.zephyr.api.controller;

import com.zephyr.api.config.TestConfig;
import com.zephyr.api.dto.request.*;
import com.zephyr.api.dto.response.AlbumResponse;
import com.zephyr.api.dto.response.PostResponse;
import com.zephyr.api.dto.response.SeriesResponse;
import com.zephyr.api.utils.H2TableCleaner;
import com.zephyr.api.utils.TestRestTemplateUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static com.zephyr.api.utils.TestConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        PostResponse post1 = restTemplateUtils.requestCreatePost(new PostCreateRequest(
                album.getId(),
                series1.getId(),
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 6, 23),
                "a",
                memoryRequestDtos1));
        List<MemoryCreateRequest> memoryRequestDtos2 = restTemplateUtils.createMemoryRequestDto(1);
        PostResponse post2 = restTemplateUtils.requestCreatePost(new PostCreateRequest(
                album.getId(),
                series1.getId(),
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 6, 2),
                "fgh",
                memoryRequestDtos2));
        List<MemoryCreateRequest> memoryRequestDtos9 = restTemplateUtils.createMemoryRequestDto(1);
        PostResponse post9 = restTemplateUtils.requestCreatePost(new PostCreateRequest(
                album.getId(),
                series1.getId(),
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 6, 2),
                "y",
                memoryRequestDtos2));
        List<MemoryCreateRequest> memoryRequestDtos4 = restTemplateUtils.createMemoryRequestDto(1);
        PostResponse post4 = restTemplateUtils.requestCreatePost(new PostCreateRequest(
                album.getId(),
                series1.getId(),
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 6, 4),
                "n",
                memoryRequestDtos2));
        List<MemoryCreateRequest> memoryRequestDtos7 = restTemplateUtils.createMemoryRequestDto(1);
        PostResponse post7 = restTemplateUtils.requestCreatePost(new PostCreateRequest(
                album.getId(),
                series1.getId(),
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 6, 5),
                "z",
                memoryRequestDtos2));
        List<MemoryCreateRequest> memoryRequestDtos3 = restTemplateUtils.createMemoryRequestDto(1);
        PostResponse post3 = restTemplateUtils.requestCreatePost(new PostCreateRequest(
                album.getId(),
                series2.getId(),
                TEST_POST_TITLE,
                TEST_POST_DESC,
                LocalDate.of(2024, 6, 3),
                "c",
                memoryRequestDtos2));

        //when


        //then

    }
}
