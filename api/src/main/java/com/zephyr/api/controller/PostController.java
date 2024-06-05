package com.zephyr.api.controller;

import com.zephyr.api.request.PostCreate;
import com.zephyr.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid PostCreate postCreate) {
//        Long result = postService.create(1L, postCreate);
        long result = 1L;
        return ResponseEntity.created(URI.create("/posts/" + result)).build();
    }
}
