package com.hyunha.batch.stock.call_kis_api_job.model;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class SymbolFeature {
    private String symbol;          // symb
    private String realtimeSymbol;  // rsym
    private String exchangeCode;    // excd

    private String name;            // 표시용(한/영)
    private BigDecimal lastPrice;   // last

    // scores 0~1
    private Double tradeAmountScore;
    private Double volumeSpikeScore;
    private Double priceMoveScore;
    private Double buyStrengthScore;

    private Double momentumScore;
}
