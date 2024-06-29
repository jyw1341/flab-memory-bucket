package com.zephyr.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "storage")
@Getter
@Setter
public class S3ConfigurationProperties {

    private String profileName;
    private String region;
    private String bucketName;
    private String endPoint;
    private List<String> thumbnails;
}
