package com.hyunha.batch.config;

import com.hyunha.batch.stock.call_kis_api_job.infra.kis.KisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Configuration
public class RestClientConfig {

    private final KisProperties kisProperties;

    @Value("${external.api.scrapy.url}")
    private String scrapyUrl;

    @Qualifier("kisRestClient")
    @Bean
    public RestClient kisRestClient(RestClient.Builder builder) {
        return builder.baseUrl(kisProperties.getBaseUrl()).build();
    }

    @Qualifier("naverNewsRestClient")
    @Bean
    public RestClient naverNewsRestClient(RestClient.Builder builder) {
        return builder.baseUrl("https://openapi.naver.com").build();
    }

    @Qualifier("scrapyRestClient")
    @Bean
    public RestClient scrapyRestClient(RestClient.Builder builder) {
        return builder.baseUrl(scrapyUrl).build();
    }


}
