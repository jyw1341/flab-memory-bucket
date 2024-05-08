package com.zephyr.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephyr.api.request.AlbumCreate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlbumController.class)
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유효한 앨범 ID를 사용하여 앨범 단건 요청을 하면 200 상태 코드를 반환한다")
    void givenValidAlbumId_whenGetAlbum_thenStatus200() throws Exception {
        Long albumId = 1L;

        mockMvc.perform(get("/albums/{albumId}", albumId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("유효한 앨범 ID를 사용하여 앨범 단건 요청을 하면 필수 필드만 반환된다")
    public void givenValidAlbumId_whenGetAlbum_thenRequiredFieldsExist() throws Exception {
        Long validAlbumId = 1L;
        String responseJson = mockMvc.perform(get("/albums/{albumId}", validAlbumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albumId").exists())
                .andExpect(jsonPath("$.albumName").exists())
                .andExpect(jsonPath("$.albumDescription").exists())
                .andExpect(jsonPath("$.albumCover").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(responseJson, Map.class);

        assertFalse(responseMap.containsKey("unexpectedField"));
    }

    @Test
    @DisplayName("존재하지 않는 앨범 ID를 사용하여 앨범 단건 요청을 하면 404 상태코드를 반환한다")
    void givenInvalidAlbumId_whenGetAlbum_thenStatus404() throws Exception {
        mockMvc.perform(get("/posts/{postId}", 1L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("유효한 앨범 ID를 사용하여 앨범 목록을 요청 하면 200상태코드와 앨범 목록이 반환된다")
    public void givenValidRequest_whenGetList_thenStatus200AndCorrectAlbums() throws Exception {
        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].albumId").value(1))
                .andExpect(jsonPath("$[0].albumName").value("테스트 앨범1"))
                .andExpect(jsonPath("$[0].albumDescription").value("hello1"))
                .andExpect(jsonPath("$[0].albumCover").value("url1"))
                .andDo(print());
    }

    @Test
    @DisplayName("유효한 앨범 생성 요청을 보내고, 201 상태코드와 응답이 정상적으로 세팅되었는지 확인")
    void givenValidRequest_whenCreateAlbum_thenStatus201AndCorrectResponse() throws Exception {
        AlbumCreate request = AlbumCreate.builder()
                .ownerId("owner")
                .albumName("테스트 앨범")
                .albumDescription("hello")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.albumId").exists())
                .andExpect(jsonPath("$.albumName").value(request.getAlbumName()))
                .andExpect(jsonPath("$.albumDescription").value(request.getAlbumDescription()))
                .andDo(print());
    }

    @Test
    @DisplayName("필수 필드만 추가한 요청을 보내고, 201 상태코드와 보내지 않은 필드가 비어있는지 확인")
    void givenValidRequest_whenCreateAlbum_thenStatus201AndOptionalFieldIsNull() throws Exception {
        AlbumCreate request = AlbumCreate.builder()
                .ownerId("owner")
                .albumName("테스트 앨범")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.albumId").exists())
                .andExpect(jsonPath("$.albumName").value(request.getAlbumName()))
                .andExpect(jsonPath("$.albumDescription").doesNotExist())
                .andExpect(jsonPath("$.albumCover").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("필수 필드인 앨범 이름이 빠졌을 때, 400 상태코드 확인")
    public void givenMissingAlbumName_whenCreateAlbum_thenBadRequest() throws Exception {
        AlbumCreate request = AlbumCreate.builder()
                .ownerId("owner")
                .albumDescription("hello")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유효한 앨범 ID를 사용한 삭제 요청을 보내고, 상태코드 204, 앨범이 성공적으로 삭제되는지 확인")
    public void givenValidAlbumId_whenDeleteAlbum_thenAlbumIsDeleted() throws Exception {
        Long validAlbumId = 1L;

        mockMvc.perform(delete("/albums/{albumId}", validAlbumId))
                .andExpect(status().isNoContent());

        //TODO: 삭제 후 해당 앨범이 더 이상 존재하지 않는지 테스트
    }

    @Test
    @DisplayName("유효하지 않은 앨범 ID를 사용한 삭제 요청을 보내고, 상태코드 404 확인")
    public void givenInvalidAlbumId_whenDeleteAlbum_thenNotFound() throws Exception {
        Long invalidAlbumId = 999L;

        mockMvc.perform(delete("/albums/{albumId}", invalidAlbumId))
                .andExpect(status().isNotFound());
    }
}