package com.zephyr.api.controller;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.AlbumMember;
import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.AlbumUpdateDTO;
import com.zephyr.api.dto.request.AlbumCreateRequest;
import com.zephyr.api.dto.request.AlbumUpdateRequest;
import com.zephyr.api.dto.response.AlbumListResponse;
import com.zephyr.api.dto.response.AlbumMemberResponse;
import com.zephyr.api.dto.response.AlbumResponse;
import com.zephyr.api.service.AlbumService;
import com.zephyr.api.service.MemberService;
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
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<AlbumResponse> create(@RequestBody AlbumCreateRequest request) {
        Member member = memberService.get(TEST_EMAIL);
        Album album = albumService.create(member, request);

        return ResponseEntity.created(URI.create("/albums/" + album.getId())).body(new AlbumResponse(album));
    }

    @GetMapping("/{albumId}")
    public AlbumResponse get(@PathVariable Long albumId) {
        Member member = memberService.get(TEST_EMAIL);
        Album album = albumService.get(member, albumId);

        return new AlbumResponse(album);
    }

    @GetMapping
    public List<AlbumListResponse> getList() {
        Long loginId = 1L;
        List<Album> albums = albumService.getList(loginId);

        return albums.stream().map(AlbumListResponse::new).toList();
    }

    @PatchMapping("/{albumId}")
    public AlbumResponse update(@PathVariable Long albumId, @RequestBody AlbumUpdateRequest request) {
        Long loginId = 1L;
        AlbumUpdateDTO dto = new AlbumUpdateDTO(
                albumId,
                loginId,
                request.getTitle(),
                request.getDescription(),
                request.getThumbnailUrl()
        );
        Album album = albumService.update(dto);

        return new AlbumResponse(album);
    }

    @DeleteMapping("/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long albumId) {
        Long loginId = 1L;
        albumService.delete(albumId, loginId);
    }

    @GetMapping("/{albumId}/members")
    public List<AlbumMemberResponse> getAlbumMembers(@PathVariable Long albumId) {
        Long loginId = 1L;
        List<AlbumMember> albumMembers = albumService.getAlbumMembers(albumId, loginId);

        return albumMembers.stream().map(AlbumMemberResponse::new).toList();
    }

    @DeleteMapping("/{albumId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void banAlbumMember(@PathVariable Long albumId, @PathVariable Long memberId) {
        Long loginId = 1L;

        albumService.banAlbumMember(albumId, loginId, memberId);
    }
}
