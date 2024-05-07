package com.zephyr.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephyr.api.request.AlbumCreate;
import com.zephyr.api.response.AlbumResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlbumController.class)
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("앨범 1개를 조회한다")
    void testGetAlbum() throws Exception {
        Long albumId = 1L;
        String albumName = "테스트 앨범";
        String albumDescription = "hello";

        mockMvc.perform(get("/albums/{albumId}", albumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albumId").value(albumId))
                .andExpect(jsonPath("$.albumName").value(albumName))
                .andExpect(jsonPath("$.albumDescription").value(albumDescription))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("앨범 리스트를 조회한다")
    void testGetAlbumList() throws Exception {
        List<AlbumResponse> result = LongStream.range(1, 11).mapToObj(value -> AlbumResponse.builder()
                .albumId(value)
                .albumName("테스트 앨범" + value)
                .albumDescription("hello " + value)
                .createdAt(LocalDateTime.now())
                .build()
        ).toList();


        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
                .andDo(print());
    }

    @Test
    @DisplayName("앨범 생성")
    void testCreateAlbum() throws Exception {
        AlbumCreate request = AlbumCreate.builder()
                .ownerId("owner")
                .albumName("테스트 앨범")
                .albumDescription("hello")
                .build();

        Long albumId = 1L;
        String albumName = "테스트 앨범";
        String albumDescription = "hello";
        LocalDateTime dateTime = LocalDateTime.now();

        AlbumResponse response = AlbumResponse.builder()
                .albumId(albumId)
                .albumName(albumName)
                .albumDescription(albumDescription)
                .createdAt(dateTime)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andDo(print());
    }
}