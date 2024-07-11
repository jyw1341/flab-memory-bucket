package com.zephyr.api.controller;

import com.zephyr.api.domain.Album;
import com.zephyr.api.dto.AlbumCreateServiceDto;
import com.zephyr.api.dto.AlbumDeleteServiceDto;
import com.zephyr.api.dto.AlbumListServiceDto;
import com.zephyr.api.dto.AlbumUpdateServiceDto;
import com.zephyr.api.dto.mapper.AlbumCreateMapper;
import com.zephyr.api.dto.mapper.AlbumDeleteMapper;
import com.zephyr.api.dto.mapper.AlbumListMapper;
import com.zephyr.api.dto.mapper.AlbumUpdateMapper;
import com.zephyr.api.dto.request.AlbumCreateRequest;
import com.zephyr.api.dto.request.AlbumListRequest;
import com.zephyr.api.dto.request.AlbumUpdateRequest;
import com.zephyr.api.dto.response.AlbumListResponse;
import com.zephyr.api.dto.response.AlbumResponse;
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
    public ResponseEntity<Void> create(@RequestBody AlbumCreateRequest request) {
        Long loginId = 1L;
        AlbumCreateServiceDto serviceDto = AlbumCreateMapper.INSTANCE.toAlbumCreateServiceDto(loginId, request);
        Album album = albumService.create(serviceDto);

        return ResponseEntity.created(URI.create("/albums/" + album.getId())).build();
    }

    @GetMapping("/{albumId}")
    public AlbumResponse get(@PathVariable Long albumId) {
        Album album = albumService.get(albumId);

        return new AlbumResponse(album);
    }

    @GetMapping
    public List<AlbumListResponse> getList(@ModelAttribute AlbumListRequest request) {
        Long loginId = 1L;
        AlbumListServiceDto serviceDto = AlbumListMapper.INSTANCE.toAlbumListServiceDto(loginId, request);
        List<Album> albums = albumService.getList(serviceDto);

        return albums.stream().map(AlbumListResponse::new).toList();
    }

    @PatchMapping("/{albumId}")
    public void update(@PathVariable Long albumId, @RequestBody AlbumUpdateRequest request) {
        Long loginId = 1L;
        AlbumUpdateServiceDto serviceDto = AlbumUpdateMapper.INSTANCE.toAlbumUpdateServiceDto(loginId, albumId, request);
        albumService.update(serviceDto);
    }

    @DeleteMapping("/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long albumId) {
        Long loginId = 1L;
        AlbumDeleteServiceDto serviceDto = AlbumDeleteMapper.INSTANCE.toAlbumDeleteServiceDto(loginId, albumId);
        albumService.delete(serviceDto);
    }
}
