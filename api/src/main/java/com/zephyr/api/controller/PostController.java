package com.zephyr.api.controller;

import com.zephyr.api.domain.Post;
import com.zephyr.api.dto.*;
import com.zephyr.api.dto.mapper.*;
import com.zephyr.api.dto.request.MemoryUpdateRequest;
import com.zephyr.api.dto.request.PostCreateRequest;
import com.zephyr.api.dto.request.PostSearchRequest;
import com.zephyr.api.dto.request.PostUpdateRequest;
import com.zephyr.api.dto.response.PostResponse;
import com.zephyr.api.dto.response.PostSearchResponse;
import com.zephyr.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping("/albums/{albumId}/posts")
    public ResponseEntity<Void> create(@PathVariable Long albumId, @RequestBody PostCreateRequest request) {
        Long loginId = 1L;
        List<MemoryCreateServiceDto> memoryCreateServiceDtos = request.getMemoryCreateRequests()
                .stream()
                .map(MemoryCreateMapper.INSTANCE::toMemoryCreateServiceDto)
                .toList();
        PostCreateServiceDto serviceDto = PostCreateMapper.INSTANCE.toPostCreateServiceDto(
                loginId,
                albumId,
                request,
                memoryCreateServiceDtos);
        Post post = postService.create(serviceDto);
        String path = String.format("/posts/%d", post.getId());

        return ResponseEntity.created(URI.create(path)).build();
    }

    @GetMapping("/albums/{albumId}/posts")
    public PostSearchResponse getList(@PathVariable Long albumId, PostSearchRequest request, Pageable pageable) {
        PostSearchServiceDto serviceDto = PostListMapper.INSTANCE.toPostListServiceDto(albumId, pageable, request);
        Page<Post> result = postService.getList(serviceDto);

        return new PostSearchResponse(result);
    }


    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        Post post = postService.get(postId);

        return new PostResponse(post);
    }

    @PostMapping("/posts/{postId}")
    public void update(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        Long loginId = 1L;

        PostUpdateServiceDto serviceDto = PostUpdateMapper.INSTANCE.toPostUpdateServiceDto(
                loginId,
                postId,
                request
        );
        postService.update(serviceDto);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        Long loginId = 1L;
        PostDeleteServiceDto serviceDto = PostDeleteMapper.INSTANCE.toPostDeleteMapper(loginId, postId);
        postService.delete(serviceDto);
    }

    @PostMapping("/posts/{postId}/memories")
    public void updateMemories(@PathVariable Long postId, @RequestBody List<MemoryUpdateRequest> requests) {
        List<MemoryUpdateServiceDto> serviceDtos = requests.stream()
                .map(MemoryUpdateMapper.INSTANCE::toMemoryUpdateServiceDto)
                .collect(Collectors.toList());

        postService.updateMemories(postId, serviceDtos);
    }
}
