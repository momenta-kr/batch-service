package com.hyunha.batch.stock.call_kis_api_job.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KisOverseasRankingApi {
    TRADE_AMOUNT("/uapi/overseas-stock/v1/ranking/trade-pbmn", "HHDFS76320010"),
    VOLUME_SPIKE("/uapi/overseas-stock/v1/ranking/volume-surge", "HHDFS76270000"),
    PRICE_CHANGE("/uapi/overseas-stock/v1/ranking/price-fluct", "HHDFS76260000"),
    BUY_STRENGTH("/uapi/overseas-stock/v1/ranking/volume-power", "HHDFS76280000");

    private final String path;
    private final String trId;
}
