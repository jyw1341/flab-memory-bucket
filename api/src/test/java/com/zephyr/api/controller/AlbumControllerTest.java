package com.zephyr.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephyr.api.request.AlbumCreate;
import com.zephyr.api.request.AlbumUpdate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("존재하는 앨범 ID를 사용 / 앨범 단건 조회 / 200 상태 코드 반환")
    void givenValidAlbumId_whenGetAlbum_thenStatus200() throws Exception {
        Long albumId = 1L;

        mockMvc.perform(get("/albums/{albumId}", albumId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("유효한 앨범 ID를 사용 / 앨범 단건 조회 / 필수 필드만 반환")
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
    @DisplayName("존재하지 않는 앨범 ID를 사용 / 앨범 단건 조회 / 404 상태코드 반환")
    void givenInvalidAlbumId_whenGetAlbum_thenStatus404() throws Exception {
        mockMvc.perform(get("/posts/{postId}", 1L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("유효한 앨범 ID를 사용 / 앨범 목록 조회 / 200 상태코드, 앨범 목록이 반환")
    public void givenValidRequest_whenGetList_thenStatus200AndCorrectAlbums() throws Exception {
        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
                .andDo(print());
    }

    @Test
    @DisplayName("모든 필드 작성 / 앨범 생성 / 201 상태코드, 요청한 필드가 세팅된 응답 반환")
    void givenValidRequest_whenCreateAlbum_thenStatus201AndCorrectResponse() throws Exception {
        AlbumCreate request = AlbumCreate.builder()
                .ownerId("owner")
                .albumTitle("테스트 앨범")
                .albumDescription("hello")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.albumId").exists())
                .andExpect(jsonPath("$.albumName").value(request.getAlbumTitle()))
                .andExpect(jsonPath("$.albumDescription").value(request.getAlbumDescription()))
                .andDo(print());
    }

    @Test
    @DisplayName("필수 필드만 작성 / 앨범 생성 / 201, 보내지 않은 필드가 비어있는 응답 반환")
    void givenValidRequest_whenCreateAlbum_thenStatus201AndOptionalFieldIsNull() throws Exception {
        AlbumCreate request = AlbumCreate.builder()
                .ownerId("owner")
                .albumTitle("테스트 앨범")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.albumId").exists())
                .andExpect(jsonPath("$.albumName").value(request.getAlbumTitle()))
                .andExpect(jsonPath("$.albumDescription").doesNotExist())
                .andExpect(jsonPath("$.albumCover").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("필수 필드인 앨범 이름 없음 / 앨범 생성 / 400 반환")
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
    @DisplayName("존재하는 앨범 ID를 사용 / 앨범 삭제 / 204, 앨범 삭제 확인")
    public void givenValidAlbumId_whenDeleteAlbum_thenAlbumIsDeleted() throws Exception {
        Long validAlbumId = 1L;

        mockMvc.perform(delete("/albums/{albumId}", validAlbumId))
                .andExpect(status().isNoContent());

        //TODO: 삭제 후 해당 앨범이 더 이상 존재하지 않는지 테스트
    }

    @Test
    @DisplayName("존재하지 않는 앨범 ID 사용 / 앨범 삭제 / 404 반환")
    public void givenInvalidAlbumId_whenDeleteAlbum_thenNotFound() throws Exception {
        Long invalidAlbumId = 999L;

        mockMvc.perform(delete("/albums/{albumId}", invalidAlbumId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("존재하는 앨범 ID 사용 / 앨범 업데이트 / 200, 변경된 앨범 정보 반환")
    public void givenValidAlbumIdAndValidRequestBody_whenUpdateAlbum_thenStatus200AndCorrectResponse() throws Exception {
        Long validAlbumId = 1L;
        AlbumUpdate request = AlbumUpdate.builder()
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/albums/{albumId}", validAlbumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albumId").value(validAlbumId))
                .andExpect(jsonPath("$.albumName").value("Updated Album Name"))
                .andExpect(jsonPath("$.albumDescription").value("Updated Album Description"))
                .andExpect(jsonPath("$.albumCover").value("url"));
    }

    @Test
    @DisplayName("없는 앨범 ID 사용 / 앨범 업데이트 / 404 반환")
    public void givenInvalidAlbumId_whenUpdateAlbum_thenStatus404() throws Exception {
        Long invalidAlbumId = 999L;

        AlbumUpdate request = AlbumUpdate.builder()
                .albumTitle("테스트 앨범")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/albums/{albumId}", invalidAlbumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("필수 필드인 앨범 제목 없음 / 앨범 업데이트 / 400 반환 ")
    public void givenRequestBodyWithMissingData_whenUpdateAlbum_thenStatus400() throws Exception {
        Long validAlbumId = 1L;

        AlbumUpdate request = AlbumUpdate.builder()
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/albums/{albumId}", validAlbumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest()); // 적절한 오류 응답 확인
    }

    @Test
    @DisplayName("유효 앨범 ID 사용 / 앨범 멤버 목록 조회 / 200, 멤버 목록 반환")
    public void givenValidAlbumId_whenGetMembers_thenStatus200AndCorrectMembers() throws Exception {
        Long validAlbumId = 1L;

        mockMvc.perform(get("/{albumId}/members", validAlbumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
                .andDo(print());
    }

    @Test
    @DisplayName("없는 앨범 ID 사용 / 앨범 멤버 목록 조회 / 404 반환")
    public void givenInvalidAlbumId_whenGetMembers_thenStatus404() throws Exception {
        Long invalidAlbumId = 999L;

        mockMvc.perform(get("/{albumId}/members", invalidAlbumId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유효 앨범 ID, 유효 멤버 ID / 앨범 멤버 삭제 / 204 반환, 멤버 목록에서 삭제 확인")
    public void givenValidAlbumIdAndValidMemberId_whenDeleteMember_thenStatus204() throws Exception {
        Long validAlbumId = 1L;
        Long validMemberId = 1L;

        mockMvc.perform(delete("/{albumId}/members/{memberId}", validAlbumId, validMemberId))
                .andExpect(status().isNoContent());

        //TODO : 멤버 목록에서 삭제 확인
    }

    @Test
    @DisplayName("없는 앨범 ID, 유효 멤버 ID / 앨범 멤버 삭제 / 404 반환")
    public void givenInvalidAlbumId_whenDeleteMember_thenStatus404() throws Exception {
        Long invalidAlbumId = 999L;
        Long validMemberId = 1L;

        mockMvc.perform(delete("/{albumId}/members/{memberId}", invalidAlbumId, validMemberId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유효 앨범 ID, 없는 멤버 ID / 앨범 멤버 삭제 / 404 반환")
    public void givenValidAlbumIdAndInvalidMemberId_whenDeleteMember_thenStatus404() throws Exception {
        Long validAlbumId = 1L;
        Long invalidMemberId = 999L;

        mockMvc.perform(delete("/{albumId}/members/{memberId}", validAlbumId, invalidMemberId))
                .andExpect(status().isNotFound());
    }
}