package com.hyunha.batch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${kis.base-url}")
    private String kisUrl;

    @Bean
    public RestClient kisRestClient(RestClient.Builder builder) {
        return builder.baseUrl(kisUrl).build();
    }


}
