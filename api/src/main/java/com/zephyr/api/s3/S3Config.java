package com.zephyr.api.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.profile-name}")
    private String profileName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${custom.s3.end-point}")
    private String endPoint;

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
