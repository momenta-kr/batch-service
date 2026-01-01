package com.hyunha.batch.stock.call_kis_api_job.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverseasStockRankingItem {

    /* ===== 식별 ===== */
    @JsonProperty("rsym")
    private String realtimeSymbol;      // D+시장+종목

    @JsonProperty("excd")
    private String exchangeCode;        // 거래소 코드

    @JsonProperty("symb")
    private String symbol;              // 종목 코드

    /* ===== 종목명 ===== */
    @JsonProperty("name")
    private String koreanName;          // 한글 종목명 (거래대금)

    @JsonProperty("knam")
    private String koreanNameAlt;       // 한글 종목명 (다른 API)

    @JsonProperty("ename")
    private String englishName;         

    @JsonProperty("enam")
    private String englishNameAlt;

    @JsonProperty("e_ordyn")
    private String tradableYn;           // 매매 가능 여부 (Y/N)

    /* ===== 현재가 ===== */
    @JsonProperty("last")
    private String lastPrice;            // 현재가

    @JsonProperty("sign")
    private String priceSign;            // 기호

    @JsonProperty("diff")
    private String priceDiff;            // 대비

    @JsonProperty("rate")
    private String priceChangeRate;      // 등락률

    /* ===== 호가 ===== */
    @JsonProperty("pask")
    private String askPrice;             // 매도 호가

    @JsonProperty("pbid")
    private String bidPrice;             // 매수 호가

    /* ===== 거래 ===== */
    @JsonProperty("tvol")
    private String tradeVolume;          // 거래량

    @JsonProperty("tamt")
    private String tradeAmount;          // 거래대금

    @JsonProperty("a_tamt")
    private String averageTradeAmount;   // 평균 거래대금

    /* ===== 랭킹 ===== */
    @JsonProperty("rank")
    private String rank;                 // 순위

    /* ===== 거래량 급증 ===== */
    @JsonProperty("n_tvol")
    private String baseTradeVolume;      // 기준 거래량

    @JsonProperty("n_diff")
    private String volumeIncrease;       // 거래량 증가량

    @JsonProperty("n_rate")
    private String volumeIncreaseRate;   // 거래량 증가율

    /* ===== 가격 급등락 ===== */
    @JsonProperty("n_base")
    private String basePrice;             // 기준 가격

    /* ===== 체결 강도 ===== */
    @JsonProperty("tpow")
    private String todayBuyStrength;     // 당일 체결 강도

    @JsonProperty("powx")
    private String buyStrength;          // 체결 강도
}
