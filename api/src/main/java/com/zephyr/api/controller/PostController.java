package com.zephyr.api.controller;

import com.zephyr.api.domain.Member;
import com.zephyr.api.domain.Post;
import com.zephyr.api.dto.request.PostCreateRequest;
import com.zephyr.api.dto.request.PostSearchRequest;
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
        Member member = memberService.get(TEST_EMAIL);
        Post post = postService.create(member, request);

        return ResponseEntity.created(URI.create("/posts/" + post.getId())).body(new PostResponse(post));
    }

    @GetMapping("/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        Member member = memberService.get(TEST_EMAIL);
        Post post = postService.get(member, postId);

        return new PostResponse(post);
    }

    @GetMapping
    public ResponseEntity<List<PostListResponse>> getList(@ModelAttribute PostSearchRequest request) {
        List<Post> result = postService.getList(request);
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(result.stream().map(PostListResponse::new).toList());
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> update(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        Member member = memberService.get(TEST_EMAIL);
        Post post = postService.update(member, postId, request);

        return ResponseEntity.ok(new PostResponse(post));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        Long loginId = 1L;
        Member member = memberService.get(TEST_EMAIL);
        postService.delete(member, postId);

        return ResponseEntity.ok().build();
    }
}
