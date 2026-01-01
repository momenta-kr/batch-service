package com.hyunha.batch.stock.call_kis_api_job.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "auth-service")
public interface TokenProvider {

    @GetMapping("/api/tokens/v1")
    String getAccessToken();
}
