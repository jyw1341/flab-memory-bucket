package com.zephyr.api.service;

import com.zephyr.api.config.S3ConfigurationProperties;
import com.zephyr.api.dto.PresignedUrlCreateServiceDto;
import com.zephyr.api.dto.response.PresignedUrlCreateResponse;
import com.zephyr.api.exception.PresignedUrlCreateFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3ConfigurationProperties s3ConfigurationProperties;

    public List<PresignedUrlCreateResponse> createPresignedUrl(List<PresignedUrlCreateServiceDto> dtos) {
        List<CompletableFuture<PresignedUrlCreateResponse>> futures = dtos.stream()
                .map(dto -> CompletableFuture.supplyAsync(() -> createPresignedUrl(dto)))
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

    public PresignedUrlCreateResponse createPresignedUrl(PresignedUrlCreateServiceDto dto) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3ConfigurationProperties.getBucketName())
                .key(createKeyName(dto))
                .build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1))
                .putObjectRequest(objectRequest)
                .build();
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

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

    public List<CompletableFuture<Void>> deleteObject(List<String> urls) {
        List<CompletableFuture<Void>> result = new ArrayList<>();
        for (String url : urls) {
            result.add(CompletableFuture.runAsync(() -> deleteObject(url)));
        }
        return result;
    }

    public void deleteObject(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3ConfigurationProperties.getBucketName())
                .key(url.getPath())
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }
}
