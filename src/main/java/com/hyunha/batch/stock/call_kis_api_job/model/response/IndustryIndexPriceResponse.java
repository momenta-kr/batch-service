package com.hyunha.batch.stock.call_kis_api_job.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * https://apiportal.koreainvestment.com/apiservice-apiservice?/uapi/domestic-stock/v1/quotations/inquire-index-category-price
 */
@Getter
@Setter
public class IndustryIndexPriceResponse {

    @JsonProperty("rt_cd")
    private String resultCode;           // 성공/실패 코드

    @JsonProperty("msg_cd")
    private String messageCode;          // 응답 코드

    @JsonProperty("msg1")
    private String message;              // 응답 메시지

    @JsonProperty("output1")
    private IndustryMarketSummary summary;

    @JsonProperty("output2")
    private List<IndustryIndexItem> industryIndexes;


    @Getter
    @Setter
    public static class IndustryMarketSummary {

        @JsonProperty("bstp_nmix_prpr")
        private String currentIndexPrice;        // 업종 지수 현재가

        @JsonProperty("bstp_nmix_prdy_vrss")
        private String changeFromPreviousDay;    // 전일 대비

        @JsonProperty("prdy_vrss_sign")
        private String changeSign;               // 전일 대비 부호 (+/-)

        @JsonProperty("bstp_nmix_prdy_ctrt")
        private String changeRate;               // 전일 대비율

        @JsonProperty("acml_vol")
        private String accumulatedVolume;        // 누적 거래량

        @JsonProperty("acml_tr_pbmn")
        private String accumulatedTradeAmount;   // 누적 거래대금

        @JsonProperty("bstp_nmix_oprc")
        private String openingPrice;              // 시가

        @JsonProperty("bstp_nmix_hgpr")
        private String highPrice;                 // 최고가

        @JsonProperty("bstp_nmix_lwpr")
        private String lowPrice;                  // 최저가

        @JsonProperty("prdy_vol")
        private String previousDayVolume;         // 전일 거래량

        @JsonProperty("ascn_issu_cnt")
        private String 상승종목수;

        @JsonProperty("down_issu_cnt")
        private String 하락종목수;

        @JsonProperty("stnr_issu_cnt")
        private String 보합종목수;

        @JsonProperty("uplm_issu_cnt")
        private String 상한종목수;

        @JsonProperty("lslm_issu_cnt")
        private String 하한종목수;

        @JsonProperty("prdy_tr_pbmn")
        private String previousDayTradeAmount;   // 전일 거래대금

        @JsonProperty("dryy_bstp_nmix_hgpr_date")
        private String yearlyHighDate;            // 연중 최고가 일자

        @JsonProperty("dryy_bstp_nmix_hgpr")
        private String yearlyHighPrice;           // 연중 최고가

        @JsonProperty("dryy_bstp_nmix_lwpr")
        private String yearlyLowPrice;            // 연중 최저가

        @JsonProperty("dryy_bstp_nmix_lwpr_date")
        private String yearlyLowDate;             // 연중 최저가 일자
    }

    @Getter
    @Setter
    public static class IndustryIndexItem {

        @JsonProperty("bstp_cls_code")
        private String industryCode;            // 업종 구분 코드

        @JsonProperty("hts_kor_isnm")
        private String industryName;            // 업종명 (HTS)

        @JsonProperty("bstp_nmix_prpr")
        private String currentIndexPrice;

        @JsonProperty("bstp_nmix_prdy_vrss")
        private String changeFromPreviousDay;

        @JsonProperty("prdy_vrss_sign")
        private String changeSign;

        @JsonProperty("bstp_nmix_prdy_ctrt")
        private String changeRate;

        @JsonProperty("acml_vol")
        private String accumulatedVolume;

        @JsonProperty("acml_tr_pbmn")
        private String accumulatedTradeAmount;

        @JsonProperty("acml_vol_rlim")
        private String volumeRatio;              // 거래량 비중

        @JsonProperty("acml_tr_pbmn_rlim")
        private String tradeAmountRatio;         // 거래대금 비중
    }
}
