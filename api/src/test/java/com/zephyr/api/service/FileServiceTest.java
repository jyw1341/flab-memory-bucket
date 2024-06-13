package com.zephyr.api.service;

import com.zephyr.api.config.S3Config;
import com.zephyr.api.dto.request.FileCreate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FileServiceTest {

    @Autowired
    FileService imageService;

    @Autowired
    S3Config s3Config;

    @Test
    @DisplayName("Presigned URL을 사용하여 파일을 업로드/ 업로드한 파일 조회/ 200 반환")
    public void shouldCreateAndUploadFileSuccessfully() throws Exception {
        //given
        Long testId = 1L;
        FileCreate fileCreate = new FileCreate(UUID.randomUUID().toString(), "jpg");
        String url = imageService.createPresignedUrl(testId, fileCreate);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("x-amz-acl", "public-read");

        HttpResponse<Void> uploadResponse = uploadFile(url, metadata, "sample text");

        // when
        HttpResponse<Void> headResponse = sendHeadRequest(testId, fileCreate);

        // then
        assertEquals(HttpURLConnection.HTTP_OK, headResponse.statusCode());
    }

    private HttpResponse<Void> uploadFile(String url, Map<String, String> metadata, String content) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        metadata.forEach(requestBuilder::header);
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = requestBuilder
                .uri(new URL(url).toURI())
                .PUT(HttpRequest.BodyPublishers.ofString(content))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    }

    private HttpResponse<Void> sendHeadRequest(Long testId, FileCreate fileCreate) throws Exception {
        String url = s3Config.getEndPoint() + "/" + s3Config.getBucketName() + "/" + fileCreate.createKeyName(String.valueOf(testId));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URL(url).toURI())
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        return httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    }
}
