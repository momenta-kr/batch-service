package com.hyunha.batch.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {


    @Bean
    public RestTemplate kisRestTemplate(RestTemplateBuilder builder) {
        return builder
                .readTimeout(Duration.ofSeconds(5))
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }
}
