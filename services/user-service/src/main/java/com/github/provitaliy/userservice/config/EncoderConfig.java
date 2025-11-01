package com.github.provitaliy.userservice.config;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncoderConfig {

    @Value("${salt}")
    private String salt;

    @Bean
    public Hashids getHashids() {
        var minHashLen = 10;
        return new Hashids(salt, minHashLen);
    }
}
