package com.hyunha.batch.stock.call_kis_api_job.infra.kis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "kis")
@Configuration
@Data
public class KisProperties {

    private String baseUrl;
    private String appKey;
    private String appSecret;
}
