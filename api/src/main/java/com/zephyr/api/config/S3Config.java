package com.zephyr.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.util.List;


@Configuration
@ConfigurationProperties(prefix = "storage")
@Getter
@Setter
public class S3Config {

    private String profileName;
    private String region;
    private String bucketName;
    private String endPoint;
    private List<String> thumbnails;

    public String getDefaultThumbnailUrl() {
        return endPoint + bucketName + thumbnails.get(0);
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.builder().profileName(profileName).build())
                .endpointOverride(URI.create(endPoint))
                .region(Region.of(region))
                .build();
    }

    @Bean
    public S3Presigner presigner() {
        return S3Presigner.builder()
                .credentialsProvider(DefaultCredentialsProvider.builder().profileName(profileName).build())
                .endpointOverride(URI.create(endPoint))
                .region(Region.of(region))
                .build();
    }
}
