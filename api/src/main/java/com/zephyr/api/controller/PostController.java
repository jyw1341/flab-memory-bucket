package com.zephyr.api.controller;

import com.zephyr.api.domain.Post;
import com.zephyr.api.dto.*;
import com.zephyr.api.dto.mapper.*;
import com.zephyr.api.dto.request.PostCreateRequest;
import com.zephyr.api.dto.request.PostListRequest;
import com.zephyr.api.dto.request.PostUpdateRequest;
import com.zephyr.api.dto.response.PostListResponse;
import com.zephyr.api.dto.response.PostResponse;
import com.zephyr.api.service.MemberService;
import com.zephyr.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static com.zephyr.api.constant.TestConstant.TEST_EMAIL;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<PostResponse> create(@RequestBody PostCreateRequest request) {
        PostCreateServiceDto serviceDto = PostCreateMapper.INSTANCE.toPostCreateServiceDto(TEST_EMAIL, request);
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
    public ResponseEntity<PostResponse> update(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        List<MemoryUpdateServiceDto> memoryUpdateServiceDtos = request.getMemoryUpdateRequests()
                .stream()
                .map(MemoryUpdateMapper.INSTANCE::toMemoryUpdateServiceDto)
                .toList();
        PostUpdateServiceDto serviceDto = PostUpdateMapper.INSTANCE.toPostUpdateServiceDto(
                TEST_EMAIL,
                postId,
                request,
                memoryUpdateServiceDtos
        );

        Post post = postService.update(serviceDto);

        return ResponseEntity.ok(new PostResponse(post));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        PostDeleteServiceDto serviceDto = PostDeleteMapper.INSTANCE.toPostDeleteMapper(TEST_EMAIL, postId);
        postService.delete(serviceDto);

        return ResponseEntity.ok().build();
    }
}
