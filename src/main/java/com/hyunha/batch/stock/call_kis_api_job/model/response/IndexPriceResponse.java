package com.hyunha.batch.stock.call_kis_api_job.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * https://apiportal.koreainvestment.com/apiservice-apiservice?/uapi/domestic-stock/v1/quotations/inquire-index-price
 * 국내업종 현재지수[v1_국내주식-063]
 */
@Getter @Setter @NoArgsConstructor @ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexPriceResponse {

    @JsonProperty("rt_cd")
    private String resultCode; // 성공/실패 여부

    @JsonProperty("msg_cd")
    private String messageCode; // 응답코드

    @JsonProperty("msg1")
    private String message; // 응답메세지

    @JsonProperty("output")
    private Output output;

    @Getter @Setter @NoArgsConstructor @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {

        @JsonProperty("bstp_nmix_prpr")
        private String industryIndexCurrentPrice; // 업종 지수 현재가

        @JsonProperty("bstp_nmix_prdy_vrss")
        private String industryIndexChangeFromPrevDay; // 업종 지수 전일 대비

        @JsonProperty("prdy_vrss_sign")
        private String changeSignFromPrevDay; // 전일 대비 부호

        @JsonProperty("bstp_nmix_prdy_ctrt")
        private String industryIndexChangeRateFromPrevDay; // 업종 지수 전일 대비율

        @JsonProperty("acml_vol")
        private String accumulatedVolume; // 누적 거래량

        @JsonProperty("prdy_vol")
        private String previousDayVolume; // 전일 거래량

        @JsonProperty("acml_tr_pbmn")
        private String accumulatedTradeAmount; // 누적 거래 대금

        @JsonProperty("prdy_tr_pbmn")
        private String previousDayTradeAmount; // 전일 거래 대금

        @JsonProperty("bstp_nmix_oprc")
        private String industryIndexOpenPrice; // 업종 지수 시가2

        @JsonProperty("prdy_nmix_vrss_nmix_oprc")
        private String openPriceChangeFromPrevIndex; // 전일 지수 대비 지수 시가2

        @JsonProperty("oprc_vrss_prpr_sign")
        private String currentVsOpenSign; // 시가2 대비 현재가 부호

        @JsonProperty("bstp_nmix_oprc_prdy_ctrt")
        private String openPriceChangeRateFromPrevDay; // 업종 지수 시가2 전일 대비율

        @JsonProperty("bstp_nmix_hgpr")
        private String industryIndexHighPrice; // 업종 지수 최고가

        @JsonProperty("prdy_nmix_vrss_nmix_hgpr")
        private String highPriceChangeFromPrevIndex; // 전일 지수 대비 지수 최고가

        @JsonProperty("hgpr_vrss_prpr_sign")
        private String currentVsHighSign; // 최고가 대비 현재가 부호

        @JsonProperty("bstp_nmix_hgpr_prdy_ctrt")
        private String highPriceChangeRateFromPrevDay; // 업종 지수 최고가 전일 대비율

        @JsonProperty("bstp_nmix_lwpr")
        private String industryIndexLowPrice; // 업종 지수 최저가

        @JsonProperty("prdy_clpr_vrss_lwpr")
        private String lowPriceChangeFromPrevClose; // 전일 종가 대비 최저가

        @JsonProperty("lwpr_vrss_prpr_sign")
        private String currentVsLowSign; // 최저가 대비 현재가 부호

        @JsonProperty("prdy_clpr_vrss_lwpr_rate")
        private String lowPriceRateFromPrevClose; // 전일 종가 대비 최저가 비율

        @JsonProperty("ascn_issu_cnt")
        private String 상승종목수; // 상승 종목 수

        @JsonProperty("uplm_issu_cnt")
        private String upperLimitCount; // 상한 종목 수

        @JsonProperty("stnr_issu_cnt")
        private String unchangedCount; // 보합 종목 수

        @JsonProperty("down_issu_cnt")
        private String declineCount; // 하락 종목 수

        @JsonProperty("lslm_issu_cnt")
        private String lowerLimitCount; // 하한 종목 수

        @JsonProperty("dryy_bstp_nmix_hgpr")
        private String yearHighIndustryIndex; // 연중업종지수최고가

        @JsonProperty("dryy_hgpr_vrss_prpr_rate")
        private String currentVsYearHighRate; // 연중 최고가 대비 현재가 비율

        @JsonProperty("dryy_bstp_nmix_hgpr_date")
        private String yearHighDate; // 연중업종지수최고가일자

        @JsonProperty("dryy_bstp_nmix_lwpr")
        private String yearLowIndustryIndex; // 연중업종지수최저가

        @JsonProperty("dryy_lwpr_vrss_prpr_rate")
        private String currentVsYearLowRate; // 연중 최저가 대비 현재가 비율

        @JsonProperty("dryy_bstp_nmix_lwpr_date")
        private String yearLowDate; // 연중업종지수최저가일자

        @JsonProperty("total_askp_rsqn")
        private String totalAskQuantity; // 총 매도호가 잔량

        @JsonProperty("total_bidp_rsqn")
        private String totalBidQuantity; // 총 매수호가 잔량

        @JsonProperty("seln_rsqn_rate")
        private String sellQuantityRate; // 매도 잔량 비율

        @JsonProperty("shnu_rsqn_rate")
        private String buyQuantityRate; // 매수 잔량 비율 (원문: 매수2)

        @JsonProperty("ntby_rsqn")
        private String netBuyQuantity; // 순매수 잔량
    }
}
