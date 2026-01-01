package com.hyunha.batch.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kis")
@Setter
@Getter
public class KisProperty {

    private String baseUrl;
    private String appKey;
    private String appSecret;
}
