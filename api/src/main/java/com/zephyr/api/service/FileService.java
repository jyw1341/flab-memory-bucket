package com.zephyr.api.service;

import com.zephyr.api.config.S3Config;
import com.zephyr.api.request.FileCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final S3Presigner s3Presigner;
    private final S3Config s3Config;

    public String createPresignedUrl(Long userId, FileCreate fileCreate) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(fileCreate.createKeyName(String.valueOf(userId)))
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))  // The URL expires in 10 minutes.
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toExternalForm();
    }
}
