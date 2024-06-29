package com.zephyr.api.controller;

import com.zephyr.api.domain.Post;
import com.zephyr.api.dto.*;
import com.zephyr.api.dto.mapper.*;
import com.zephyr.api.dto.request.PostCreateRequest;
import com.zephyr.api.dto.request.PostListRequest;
import com.zephyr.api.dto.request.PostUpdateRequest;
import com.zephyr.api.dto.response.PostListResponse;
import com.zephyr.api.dto.response.PostResponse;
import com.zephyr.api.service.PostService;
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

    @PostMapping
    public ResponseEntity<PostResponse> create(@RequestBody PostCreateRequest request) {
        Long loginId = 1L;
        PostCreateServiceDto serviceDto = PostCreateMapper.INSTANCE.toPostCreateServiceDto(loginId, request);
        Post post = postService.create(serviceDto);

        return ResponseEntity.created(URI.create("/posts/" + post.getId())).body(new PostResponse(post));
    }

    @GetMapping("/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        Post post = postService.get(postId);

        return new PostResponse(post);
    }

    @GetMapping
    public ResponseEntity<List<PostListResponse>> getList(@ModelAttribute PostListRequest request) {
        PostListServiceDto serviceDto = PostListMapper.INSTANCE.toPostListServiceDto(request);

        List<Post> result = postService.getList(serviceDto);

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(result.stream().map(PostListResponse::new).toList());
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Void> update(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        Long loginId = 1L;
        List<MemoryUpdateServiceDto> memoryUpdateServiceDtos = request.getMemoryUpdateRequests()
                .stream()
                .map(MemoryUpdateMapper.INSTANCE::toMemoryUpdateServiceDto)
                .toList();
        PostUpdateServiceDto serviceDto = PostUpdateMapper.INSTANCE.toPostUpdateServiceDto(
                loginId,
                postId,
                request,
                memoryUpdateServiceDtos
        );
        postService.update(serviceDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        Long loginId = 1L;
        PostDeleteServiceDto serviceDto = PostDeleteMapper.INSTANCE.toPostDeleteMapper(loginId, postId);
        postService.delete(serviceDto);

        return ResponseEntity.ok().build();
    }
}
