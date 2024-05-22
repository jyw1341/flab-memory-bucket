package com.zephyr.api.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final Environment env;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.builder().profileName(env.getProperty("cloud.aws.credentials.profile-name")).build())
                .endpointOverride(URI.create(env.getProperty("custom.s3.end-point", "https://kr.object.ncloudstorage.com")))
                .region(Region.of(env.getProperty("cloud.aws.region.static", "kr-standard")))
                .build();
    }

    @Bean
    public S3Presigner presigner() {
        return S3Presigner.builder()
                .credentialsProvider(DefaultCredentialsProvider.builder().profileName(env.getProperty("cloud.aws.credentials.profile-name")).build())
                .endpointOverride(URI.create(env.getProperty("custom.s3.end-point", "https://kr.object.ncloudstorage.com")))
                .region(Region.of(env.getProperty("cloud.aws.region.static", "kr-standard")))
                .build();
    }
}
