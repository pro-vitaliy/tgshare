package com.github.provitaliy.fileservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ConfigurationProperties(prefix = "minio")
@Component
public class MinioProperties {
    private String url;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private Integer defaultTtlDays;
}
