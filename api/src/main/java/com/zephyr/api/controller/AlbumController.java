package com.zephyr.api.controller;

import com.zephyr.api.request.AlbumCreate;
import com.zephyr.api.request.AlbumMemberRequest;
import com.zephyr.api.request.AlbumUpdate;
import com.zephyr.api.response.AlbumListResponse;
import com.zephyr.api.response.AlbumMemberResponse;
import com.zephyr.api.response.AlbumResponse;
import com.zephyr.api.response.AlbumUpdateResponse;
import com.zephyr.api.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody AlbumCreate request) {
        Long result = albumService.create(1L, request);
        return ResponseEntity.created(URI.create("/albums/" + result)).build();
    }

    @GetMapping("/{albumId}")
    public AlbumResponse get(@PathVariable Long albumId) {
        return albumService.get(albumId, 1L);
    }

    @GetMapping
    public List<AlbumListResponse> getList() {
        return albumService.getList(1L);
    }

    @DeleteMapping("/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long albumId) {
        Long loginId = 1L;
        albumService.delete(albumId, loginId);
    }

    @PatchMapping("/{albumId}")
    public AlbumUpdateResponse update(@PathVariable Long albumId, @RequestBody AlbumUpdate request) {
        Long loginId = 1L;
        return albumService.update(albumId, loginId, request);
    }

    @PostMapping("/{albumId}/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createAlbumMember(@PathVariable Long albumId, @RequestBody AlbumMemberRequest request) {
        Long loginId = 1L;
        albumService.createAlbumMember(albumId, loginId, request);
    }

    @GetMapping("/{albumId}/members")
    public List<AlbumMemberResponse> getAlbumMembers(@PathVariable Long albumId) {
        return albumService.getAlbumMembers(albumId);
    }

    @DeleteMapping("/{albumId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbumMember(@PathVariable Long albumId, @PathVariable Long memberId) {
        Long loginId = 1L;
        albumService.deleteAlbumMember(albumId, loginId, memberId);
    }
}
