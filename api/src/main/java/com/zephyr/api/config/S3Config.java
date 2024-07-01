package com.zephyr.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;


@Configuration
@EnableConfigurationProperties(S3ConfigurationProperties.class)
@RequiredArgsConstructor
public class S3Config {

    private final S3ConfigurationProperties properties;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.builder().profileName(properties.getProfileName()).build())
                .endpointOverride(URI.create(properties.getEndPoint()))
                .region(Region.of(properties.getRegion()))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .credentialsProvider(DefaultCredentialsProvider.builder().profileName(properties.getProfileName()).build())
                .endpointOverride(URI.create(properties.getEndPoint()))
                .region(Region.of(properties.getRegion()))
                .build();
    }
}
