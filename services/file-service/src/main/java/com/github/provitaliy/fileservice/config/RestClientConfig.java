package com.github.provitaliy.fileservice.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.custom()
                .disableCookieManagement()
                .evictExpiredConnections()
                .build();
    }
}
