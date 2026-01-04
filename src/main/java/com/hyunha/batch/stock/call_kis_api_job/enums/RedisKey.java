package com.hyunha.batch.stock.call_kis_api_job.enums;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum RedisKey {

    INDUSTRY_INDEX_PRICE("stock:industry:index:price", Duration.ofDays(30)),
    TOP_GAINERS("stock:top:gainers", Duration.ofDays(30)),
    TOP_LOSERS("stock:top:losers", Duration.ofDays(30)),
    INDEX_PRICE("stock:index:price", Duration.ofDays(30))
    ;

    private String key;
    private Duration ttl;

    RedisKey(String key, Duration ttl) {
        this.key = key;
        this.ttl = ttl;
    }


}
