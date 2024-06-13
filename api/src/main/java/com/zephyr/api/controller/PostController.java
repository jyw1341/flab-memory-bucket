package com.zephyr.api.controller;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Member;
import com.zephyr.api.domain.Post;
import com.zephyr.api.domain.Series;
import com.zephyr.api.request.PostCreate;
import com.zephyr.api.request.PostSearch;
import com.zephyr.api.request.PostUpdate;
import com.zephyr.api.response.PostListResponse;
import com.zephyr.api.response.PostResponse;
import com.zephyr.api.service.AlbumService;
import com.zephyr.api.service.MemberService;
import com.zephyr.api.service.PostService;
import com.zephyr.api.service.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final AlbumService albumService;
    private final MemberService memberService;
    private final SeriesService seriesService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid PostCreate postCreate) {
        Long loginId = 1L;
        Album album = albumService.get(postCreate.getAlbumId(), loginId);
        Member member = memberService.get(loginId);
        Series series = seriesService.get(postCreate.getSeriesId());
        Post result = postService.create(album, member, series, postCreate);

        return ResponseEntity.created(URI.create("/posts/" + 1L)).build();
    }

    @GetMapping("/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        Long loginId = 1L;
        Post post = postService.get(postId);
        albumService.validReadPermission(post.getAlbum(), loginId);
        return new PostResponse(post);
    }

    @GetMapping
    public ResponseEntity<List<PostListResponse>> getList(@ModelAttribute PostSearch postSearch) {
        Long loginId = 1L;
        List<Post> result = postService.getList(postSearch);
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        albumService.validReadPermission(result.get(0).getAlbum(), loginId);
        return ResponseEntity.ok().body(result.stream().map(PostListResponse::new).toList());
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Void> update(@PathVariable Long postId, @RequestBody PostUpdate postUpdate) {
        Long loginId = 1L;
        Post post = postService.get(postId);
        Series series = seriesService.get(postUpdate.getSeriesId());
        Post result = postService.update(loginId, postId, postUpdate);
        return null;
    }
}
