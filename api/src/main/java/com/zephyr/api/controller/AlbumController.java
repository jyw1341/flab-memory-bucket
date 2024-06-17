package com.zephyr.api.controller;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.AlbumMember;
import com.zephyr.api.dto.mapper.*;
import com.zephyr.api.dto.request.AlbumCreateRequest;
import com.zephyr.api.dto.request.AlbumUpdateRequest;
import com.zephyr.api.dto.response.AlbumListResponse;
import com.zephyr.api.dto.response.AlbumMemberResponse;
import com.zephyr.api.dto.response.AlbumResponse;
import com.zephyr.api.dto.service.*;
import com.zephyr.api.service.AlbumMemberService;
import com.zephyr.api.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static com.zephyr.api.constant.TestConstant.TEST_EMAIL;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumMemberService albumMemberService;

    @PostMapping
    public ResponseEntity<AlbumResponse> create(@RequestBody AlbumCreateRequest request) {
        AlbumCreateServiceDto serviceDto = AlbumCreateMapper.INSTANCE.toAlbumCreateServiceDto(TEST_EMAIL, request);

        Album album = albumService.create(serviceDto);

        return ResponseEntity.created(URI.create("/albums/" + album.getId())).body(new AlbumResponse(album));
    }

    @GetMapping("/{albumId}")
    public AlbumResponse get(@PathVariable Long albumId) {
        Album album = albumService.get(albumId);

        return new AlbumResponse(album);
    }

    @GetMapping
    public List<AlbumListResponse> getList() {
        AlbumListServiceDto serviceDto = AlbumListMapper.INSTANCE.toAlbumUpdateServiceDto(TEST_EMAIL);

        List<Album> albums = albumService.getList(serviceDto);

        return albums.stream().map(AlbumListResponse::new).toList();
    }

    @PatchMapping("/{albumId}")
    public AlbumResponse update(@PathVariable Long albumId, @RequestBody AlbumUpdateRequest request) {
        AlbumUpdateServiceDto serviceDto = AlbumUpdateMapper.INSTANCE.toAlbumUpdateServiceDto(TEST_EMAIL, albumId, request);

        Album album = albumService.update(serviceDto);

        return new AlbumResponse(album);
    }

    @DeleteMapping("/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long albumId) {
        AlbumDeleteServiceDto serviceDto = AlbumDeleteMapper.INSTANCE.toAlbumDeleteServiceDto(TEST_EMAIL, albumId);

        albumService.delete(serviceDto);
    }

    @GetMapping("/{albumId}/members")
    public List<AlbumMemberResponse> getAlbumMembers(@PathVariable Long albumId) {
        AlbumMemberListServiceDto serviceDto = AlbumMemberListMapper.INSTANCE.toAlbumMemberListDto(TEST_EMAIL, albumId);
        List<AlbumMember> albumMembers = albumMemberService.getListByAlbumId(serviceDto);

        return albumMembers.stream().map(AlbumMemberResponse::new).toList();
    }

    @DeleteMapping("/{albumId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbumMember(@PathVariable Long albumId, @PathVariable("memberId") Long targetId) {
        AlbumMemberDeleteServiceDto serviceDto = AlbumMemberDeleteMapper.INSTANCE.toAlbumMemberDeleteServiceDto(albumId, targetId);

        albumMemberService.delete(serviceDto);
    }
}
