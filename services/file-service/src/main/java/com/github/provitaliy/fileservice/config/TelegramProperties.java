package com.github.provitaliy.fileservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@ConfigurationProperties(prefix = "telegram")
@Configuration
public class TelegramProperties {
    private String botToken;
    private String fileInfoUrl;
    private String fileStorageUrl;
}
