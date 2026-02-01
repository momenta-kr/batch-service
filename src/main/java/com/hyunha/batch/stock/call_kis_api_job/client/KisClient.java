package com.hyunha.batch.stock.call_kis_api_job.client;

import com.hyunha.batch.stock.call_kis_api_job.model.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kis-gateway")
public interface KisClient {

    @GetMapping("/api/kis/v1/domestic-stock-current-price")
    DomesticStockCurrentPriceResponse fetchDomesticStockCurrentPrice(
            @RequestParam String stockCode);

    @GetMapping("/api/kis/v1/top-gainers")
    FluctuationResponse fetchTopGainers();

    @GetMapping("/api/kis/v1/top-losers")
    FluctuationResponse fetchTopLosers();

    @GetMapping("/api/kis/v1/index-price")
    IndexPriceResponse fetchIndexPrice();

    @GetMapping("/api/kis/v1/industry-index-price")
    IndustryIndexPriceResponse fetchIndustryIndexPrice();

    @GetMapping("/api/kis/v1/market-cap-rank")
    MarketCapRankResponse fetchMarketCapRanking();

    @GetMapping("/api/kis/v1/volume-rank")
    VolumeRankResponse fetchVolumeRanking();

    @GetMapping("/api/kis/v1/stocks-of-interest-rank")
    StocksOfInterestRankResponse fetchStocksOfInterestRanking();

    @GetMapping("/api/kis/v1/trade-strength-rank")
    TradeStrengthResponse fetchTradeStrengthRanking();
}
