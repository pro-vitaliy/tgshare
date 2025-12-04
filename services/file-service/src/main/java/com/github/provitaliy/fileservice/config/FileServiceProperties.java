package com.github.provitaliy.fileservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ConfigurationProperties(prefix = "file-service")
@Component
public class FileServiceProperties {
    private String url;
    private String host;
    private Integer port;
    private String downloadEndpoint;
}
