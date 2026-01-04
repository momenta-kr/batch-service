package com.hyunha.batch.config;

import com.hyunha.batch.stock.call_kis_api_job.infra.kis.KisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@RequiredArgsConstructor
@Configuration
public class RestTemplateConfig {

    private final KisProperties kisProperties;


    @Bean
    public RestTemplate kisRestTemplate(RestTemplateBuilder builder) {
        return builder


                .readTimeout(Duration.ofSeconds(5))
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }
}
