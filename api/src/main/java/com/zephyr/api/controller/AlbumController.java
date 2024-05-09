package com.zephyr.api.controller;

import com.zephyr.api.request.AlbumCreate;
import com.zephyr.api.request.AlbumUpdate;
import com.zephyr.api.request.MemoryCreate;
import com.zephyr.api.response.AlbumMemberResponse;
import com.zephyr.api.response.AlbumResponse;
import com.zephyr.api.response.MemoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/albums")
public class AlbumController {

    @GetMapping("/{albumId}")
    public AlbumResponse get(@PathVariable Long albumId) {
        AlbumResponse response = AlbumResponse.builder()
                .albumId(albumId)
                .albumName("테스트 앨범")
                .albumDescription("hello")
                .albumCover("url")
                .createdAt(LocalDateTime.now())
                .build();

        return response;
    }

    @GetMapping
    public List<AlbumResponse> getList() {
        List<AlbumResponse> response = LongStream.range(1, 11).mapToObj(value -> AlbumResponse.builder()
                .albumId(value)
                .albumName("테스트 앨범" + value)
                .albumDescription("hello" + value)
                .albumCover("url" + value)
                .createdAt(LocalDateTime.now())
                .build()
        ).toList();

        return response;
    }

    @PostMapping
    public ResponseEntity<AlbumResponse> create(@RequestBody AlbumCreate request) {
        AlbumResponse response = AlbumResponse.builder()
                .albumId(1L)
                .albumName(request.getAlbumTitle())
                .albumDescription(request.getAlbumDescription())
                .albumCover("url")
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.created(URI.create("/albums/1")).body(response);
    }

    @DeleteMapping("/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long albumId) {

    }

    @PatchMapping("/{albumId}")
    public AlbumResponse update(@PathVariable Long albumId, @RequestBody AlbumUpdate request) {
        AlbumResponse response = AlbumResponse.builder()
                .albumId(albumId)
                .albumName(request.getAlbumTitle())
                .albumDescription(request.getAlbumDescription())
                .albumCover("url")
                .createdAt(LocalDateTime.now())
                .build();

        return response;
    }

    @GetMapping("/{albumId}/members")
    public List<AlbumMemberResponse> getMembers(@PathVariable Long albumId) {
        List<AlbumMemberResponse> response = LongStream.range(1, 11).mapToObj(value -> AlbumMemberResponse.builder()
                .memberId(value)
                .memberName("name" + value)
                .memberProfileImage("url" + value)
                .registerDate(LocalDateTime.now())
                .build()
        ).toList();

        return response;
    }

    @PostMapping("/{albumId}/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMember(@PathVariable Long albumId) {

    }

    @DeleteMapping("/{albumId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long albumId, @PathVariable Long memberId) {

    }

    @GetMapping("/{albumId}/memories")
    public List<MemoryResponse> getMemories(@PathVariable Long albumId) {
        List<MemoryResponse> response = LongStream.range(1, 11).mapToObj(value -> MemoryResponse.builder()
                .memoryTitle("title" + value)
                .memoryDescription("description" + value)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build()
        ).toList();

        return response;
    }

    @PostMapping("/{albumId}/memories")
    public ResponseEntity<MemoryResponse> createMemory(@PathVariable Long albumId, @RequestBody MemoryCreate request) {
        MemoryResponse response = MemoryResponse.builder()
                .memoryTitle("title")
                .memoryDescription("description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        return ResponseEntity.created(URI.create("/memories/1")).body(response);
    }
}
