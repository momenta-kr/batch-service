package com.hyunha.batch.stock.call_kis_api_job.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FluctuationResponse {

    @JsonProperty("rt_cd")
    private String resultCode;           // 성공/실패 코드

    @JsonProperty("msg_cd")
    private String messageCode;          // 응답 코드

    @JsonProperty("msg1")
    private String message;              // 응답 메시지

    @JsonProperty("output")
    private List<Output> output;

    @Data
    public static class Output {
        /**
         * 주식 단축 종목코드
         */
        @JsonProperty("stck_shrn_iscd")
        private String shortStockCode;

        /**
         * 데이터 순위
         */
        @JsonProperty("data_rank")
        private String rank;

        /**
         * HTS 한글 종목명
         */
        @JsonProperty("hts_kor_isnm")
        private String stockName;

        /**
         * 현재가
         */
        @JsonProperty("stck_prpr")
        private String currentPrice;

        /**
         * 전일 대비 값
         */
        @JsonProperty("prdy_vrss")
        private String changeFromPrevDay;

        /**
         * 전일 대비 부호 (+ / - / 0)
         */
        @JsonProperty("prdy_vrss_sign")
        private String changeSign;

        /**
         * 전일 대비율 (%)
         */
        @JsonProperty("prdy_ctrt")
        private String changeRateFromPrevDay;

        /**
         * 누적 거래량
         */
        @JsonProperty("acml_vol")
        private String accumulatedVolume;

        /**
         * 최고가
         */
        @JsonProperty("stck_hgpr")
        private String highPrice;

        /**
         * 최고가 발생 시간
         */
        @JsonProperty("hgpr_hour")
        private String highPriceTime;

        /**
         * 누적 최고가 일자
         */
        @JsonProperty("acml_hgpr_date")
        private String accumulatedHighPriceDate;

        /**
         * 최저가
         */
        @JsonProperty("stck_lwpr")
        private String lowPrice;

        /**
         * 최저가 발생 시간
         */
        @JsonProperty("lwpr_hour")
        private String lowPriceTime;

        /**
         * 누적 최저가 일자
         */
        @JsonProperty("acml_lwpr_date")
        private String accumulatedLowPriceDate;

        /**
         * 최저가 대비 현재가 비율
         */
        @JsonProperty("lwpr_vrss_prpr_rate")
        private String rateFromLowPrice;

        /**
         * 지정일 종가 대비 현재가 비율
         */
        @JsonProperty("dsgt_date_clpr_vrss_prpr_rate")
        private String rateFromDesignatedClosePrice;

        /**
         * 연속 상승 일수
         */
        @JsonProperty("cnnt_ascn_dynu")
        private String consecutiveRiseDays;

        /**
         * 최고가 대비 현재가 비율
         */
        @JsonProperty("hgpr_vrss_prpr_rate")
        private String rateFromHighPrice;

        /**
         * 연속 하락 일수
         */
        @JsonProperty("cnnt_down_dynu")
        private String consecutiveFallDays;

        /**
         * 시가 대비 현재가 부호
         */
        @JsonProperty("oprc_vrss_prpr_sign")
        private String openPriceChangeSign;

        /**
         * 시가 대비 현재가
         */
        @JsonProperty("oprc_vrss_prpr")
        private String changeFromOpenPrice;

        /**
         * 시가 대비 현재가 비율
         */
        @JsonProperty("oprc_vrss_prpr_rate")
        private String rateFromOpenPrice;

        /**
         * 기간 등락 값
         */
        @JsonProperty("prd_rsfl")
        private String periodChangeValue;

        /**
         * 기간 등락 비율
         */
        @JsonProperty("prd_rsfl_rate")
        private String periodChangeRate;
    }
}
