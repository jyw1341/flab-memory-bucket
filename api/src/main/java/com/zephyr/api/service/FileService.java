package com.zephyr.api.service;

import com.zephyr.api.config.S3Config;
import com.zephyr.api.dto.response.PresignedUrlCreateResponse;
import com.zephyr.api.dto.service.PresignedUrlCreateServiceDto;
import com.zephyr.api.exception.PresignedUrlCreateFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final S3Config s3Config;

    public List<PresignedUrlCreateResponse> createPresignedUrl(List<PresignedUrlCreateServiceDto> dtos) {
        List<CompletableFuture<PresignedUrlCreateResponse>> futures = dtos.stream()
                .map(dto -> CompletableFuture.supplyAsync(() -> createPresignedUrlForDto(dto)))
                .toList();

        CompletableFuture<List<PresignedUrlCreateResponse>> result = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new PresignedUrlCreateFailException(e.getMessage());
        }
    }

    private PresignedUrlCreateResponse createPresignedUrlForDto(PresignedUrlCreateServiceDto dto) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(createKeyName(dto))
                .build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1))
                .putObjectRequest(objectRequest)
                .build();
        PresignedPutObjectRequest presignedRequest = s3Config.getPresigner().presignPutObject(presignRequest);

        return new PresignedUrlCreateResponse(dto.getFileIndex(), presignedRequest.url().toExternalForm());
    }

    private String createKeyName(PresignedUrlCreateServiceDto dto) {
        String fileName = dto.getFileName();

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            throw new PresignedUrlCreateFailException("잘못된 파일 형식");
        }

        String extension = fileName.substring(dotIndex);
        String uuid = UUID.randomUUID().toString();

        return dto.getMemberId() + "/" + uuid + extension;
    }

    public void deleteObjects(List<String> urls) {
        for (String url : urls) {
            CompletableFuture.runAsync(() -> deleteObject(url));
        }
    }

    private void deleteObject(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(url.getPath())
                .build();
        s3Config.getS3Client().deleteObject(deleteObjectRequest);
    }
}
