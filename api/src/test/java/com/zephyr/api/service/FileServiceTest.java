package com.zephyr.api.service;

import com.zephyr.api.config.S3Config;
import com.zephyr.api.dto.response.PresignedUrlCreateResponse;
import com.zephyr.api.dto.service.PresignedUrlCreateServiceDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private S3Config s3Config;

    @InjectMocks
    private FileService fileService;

    @Test
    @DisplayName("presigned url 생성 성공")
    public void testCreatePresignedUrl_success() throws Exception {
        //given
        List<PresignedUrlCreateServiceDto> serviceDtos = createServiceDtos();
        URL url = new URL("http://example.com");
        S3Presigner s3Presigner = mock(S3Presigner.class);
        when(s3Config.getPresigner()).thenReturn(s3Presigner);
        when(s3Config.getBucketName()).thenReturn("bucketName");
        PresignedPutObjectRequest presignedRequest = mock(PresignedPutObjectRequest.class);
        when(presignedRequest.url()).thenReturn(url);
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presignedRequest);

        //when
        List<PresignedUrlCreateResponse> responses = fileService.createPresignedUrl(serviceDtos);

        //then
        assertEquals(serviceDtos.size(), responses.size());
        for (PresignedUrlCreateServiceDto serviceDto : serviceDtos) {
            PresignedUrlCreateResponse presignedUrlCreateResponse = responses.stream()
                    .filter(response -> response.getFileIndex().equals(serviceDto.getFileIndex()))
                    .findFirst()
                    .orElseThrow();
            assertNotNull(presignedUrlCreateResponse);
            assertNotNull(presignedUrlCreateResponse.getUrl());
        }

        verify(presignedRequest, times(serviceDtos.size())).url();
    }

    private List<PresignedUrlCreateServiceDto> createServiceDtos() {
        List<PresignedUrlCreateServiceDto> dtos = new ArrayList<>();

        dtos.add(new PresignedUrlCreateServiceDto(1L, 0, "test1.jpg", 1024L));
        dtos.add(new PresignedUrlCreateServiceDto(1L, 1, "test2.png", 2024L));
        dtos.add(new PresignedUrlCreateServiceDto(1L, 2, "test3.jpeg", 2024L));
        dtos.add(new PresignedUrlCreateServiceDto(1L, 3, "test4.mp4", 2024L));

        return dtos;
    }

    @Test
    @DisplayName("오브젝트 삭제 성공")
    void successDeleteObjects() {
        //given
        S3Client s3Client = mock(S3Client.class);
        when(s3Config.getS3Client()).thenReturn(s3Client);
        when(s3Config.getBucketName()).thenReturn("bucketName");

        List<String> urls = new ArrayList<>();
        urls.add("https://example.com/1/9b3b4472-91af-4603-94a3-515f8d45769c.jpg");
        urls.add("https://example.com/1/9b3b4472-91af-4603-94a3-515f8d45769c.jpg");
        urls.add("https://example.com/1/9b3b4472-91af-4603-94a3-515f8d45769c.jpg");
        //when
        List<CompletableFuture<Void>> completableFutures = fileService.deleteObjects(urls);
        CompletableFuture<Void> allOf = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
        allOf.join();

        //then
        verify(s3Client, times(urls.size())).deleteObject(any(DeleteObjectRequest.class));
    }
}
