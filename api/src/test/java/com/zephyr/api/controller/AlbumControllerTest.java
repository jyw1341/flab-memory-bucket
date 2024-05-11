package com.zephyr.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephyr.api.request.AlbumCreate;
import com.zephyr.api.request.AlbumUpdate;
import com.zephyr.api.request.MemoryCreate;
import com.zephyr.api.response.AlbumResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AlbumControllerTest {

    public static final String LOCALHOST = "http://localhost:";
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static String createUrl(String baseUrl, int port, String path) {
        return baseUrl + port + path;
    }

    @Test
    @DisplayName("존재하는 앨범 ID를 사용 / 앨범 단건 조회 / 200, 앨범 정보 반환")
    void givenValidAlbumId_whenGetAlbum_thenStatus200() throws Exception {
        //given
        String testTitle = "테스트 앨범";
        AlbumCreate request = AlbumCreate.builder().albumTitle(testTitle).build();
        ResponseEntity<AlbumResponse> responseEntity = restTemplate.postForEntity(
                createUrl(LOCALHOST, port, "/albums"),
                request,
                AlbumResponse.class
        );
        URI location = responseEntity.getHeaders().getLocation();

        //when
        ResponseEntity<AlbumResponse> resultEntity = restTemplate.getForEntity(
                createUrl(LOCALHOST, port, location.toString()),
                AlbumResponse.class
        );

        //then
        assertEquals(HttpStatus.OK, resultEntity.getStatusCode());
        assertEquals(testTitle, resultEntity.getBody().getAlbumTitle());
    }

    @Test
    @DisplayName("존재하지 않는 앨범 ID를 사용 / 앨범 단건 조회 / 404 상태코드 반환")
    void givenInvalidAlbumId_whenGetAlbum_thenStatus404() throws Exception {
        mockMvc.perform(get("/albums/{albumId}", 1L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("앨범을 10개 생성 / 앨범 목록 조회 / 200 상태코드, 앨범 10개 반환")
    public void givenValidRequest_whenGetList_thenStatus200AndCorrectAlbums() throws Exception {
        //given
        int resultSize = 10;
        for (int i = 0; i < resultSize; i++) {
            AlbumCreate request = AlbumCreate.builder().albumTitle("테스트 앨범" + i).build();
            restTemplate.postForEntity(
                    createUrl(LOCALHOST, port, "/albums"),
                    request,
                    AlbumResponse.class
            );
        }

        //when
        ResponseEntity<AlbumResponse[]> resultEntity = restTemplate.getForEntity(
                createUrl(LOCALHOST, port, "/albums"),
                AlbumResponse[].class
        );

        //then
        assertEquals(resultSize, resultEntity.getBody().length);
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

    @Test
    @DisplayName("유효 앨범 ID / 기억 목록 조회 / 200, 기억 목록 반환")
    public void givenValidAlbumId_whenGetMemories_thenStatus200AndCorrectMemories() throws Exception {
        Long validAlbumId = 1L;

        mockMvc.perform(get("/{albumId}/memories", validAlbumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
                .andDo(print());
    }

    @Test
    @DisplayName("없는 앨범 ID / 기억 목록 조회 / 404 반환")
    public void givenInvalidAlbumId_whenGetMemories_thenStatus404() throws Exception {
        Long invalidAlbumId = 999L;

        mockMvc.perform(get("/{albumId}/memories", invalidAlbumId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유효 앨범 ID, 모든 필드가 존재 / 기억 생성 / 201 반환, 생성된 기억 반환")
    public void givenValidAlbumIdAndValidRequestBody_whenCreateMemory_thenStatus201AndCorrectResponse() throws Exception {
        Long validAlbumId = 1L;

        MemoryCreate request = new MemoryCreate("제목", "내용", LocalDateTime.now(), LocalDateTime.now());
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/{albumId}/memories", validAlbumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.memoryTitle").value("title"))
                .andExpect(jsonPath("$.memoryDescription").value("description"));
    }

    @Test
    @DisplayName("참여하지 않는 앨범 ID / 기억 생성 / 404 반환 ")
    public void givenInvalidAlbumId_whenCreateMemory_thenStatus404() throws Exception {
        Long invalidAlbumId = 999L;
        MemoryCreate request = new MemoryCreate("제목", "내용", LocalDateTime.now(), LocalDateTime.now());
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/{albumId}/memories", invalidAlbumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("참여하지 않는 앨범 ID / 기억 생성 / 403 반환 ")
    public void givenNotMemberAlbumId_whenCreateMemory_thenStatus403() throws Exception {
        Long invalidAlbumId = 999L;
        MemoryCreate request = new MemoryCreate("제목", "내용", LocalDateTime.now(), LocalDateTime.now());
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/{albumId}/memories", invalidAlbumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("필수 필드가 없는 경우 / 기억 생성 / 400 반환")
    public void givenValidAlbumIdAndInvalidRequestBody_whenCreateMemory_thenStatus400() throws Exception {
        Long validAlbumId = 1L;
        MemoryCreate request = new MemoryCreate("제목", "내용", LocalDateTime.now(), LocalDateTime.now());
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/{albumId}/memories", validAlbumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
