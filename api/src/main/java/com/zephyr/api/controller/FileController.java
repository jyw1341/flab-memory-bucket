package com.zephyr.api.controller;

import com.zephyr.api.request.FileCreate;
import com.zephyr.api.response.FileResponse;
import com.zephyr.api.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<FileResponse> createUploadUrl(@RequestBody FileCreate request) {
        String result = fileService.createPresignedUrl(1L, request);
        return ResponseEntity.ok().body(new FileResponse(result));
    }

}
