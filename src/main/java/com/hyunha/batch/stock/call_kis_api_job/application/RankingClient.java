package com.hyunha.batch.stock.call_kis_api_job.application;

import com.hyunha.batch.stock.call_kis_api_job.model.response.*;

public interface RankingClient {

    MarketCapRankResponse fetchMarketCapRanking();
    VolumeRankResponse fetchVolumeRanking();
    FluctuationResponse fetchTopGainers();
    FluctuationResponse fetchTopLosers();
    StocksOfInterestRankResponse fetchStocksOfInterestRanking();
    TradeStrengthResponse fetchTradeStrengthRanking();
}
