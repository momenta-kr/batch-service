package com.hyunha.batch.stock.call_kis_api_job.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TradeStrengthResponse {

    /**
     * 응답상세 (Object Array)
     */
    @JsonProperty("output")
    public List<Output> outputList;

    /**
     * output 배열 요소
     */
    @Data
    public static class Output {
        /**
         * 주식 단축 종목코드 (String, 9)
         */
        @JsonProperty("stck_shrn_iscd")
        private String shortStockCode;

        /**
         * 데이터 순위 (String, 10)
         */
        @JsonProperty("data_rank")
        private String dataRank;

        /**
         * HTS 한글 종목명 (String, 40)
         */
        @JsonProperty("hts_kor_isnm")
        private String htsKoreanName;

        /**
         * 주식 현재가 (String, 10)
         */
        @JsonProperty("stck_prpr")
        private String currentPrice;

        /**
         * 전일 대비 (String, 10)
         */
        @JsonProperty("prdy_vrss")
        private String prevDayDiff;

        /**
         * 전일 대비 부호 (String, 1)
         */
        @JsonProperty("prdy_vrss_sign")
        private String prevDayDiffSign;

        /**
         * 전일 대비율 (String, 82)
         */
        @JsonProperty("prdy_ctrt")
        private String prevDayChangeRate;

        /**
         * 누적 거래량 (String, 18)
         */
        @JsonProperty("acml_vol")
        private String accumulatedVolume;

        /**
         * 당일 체결강도 (String, 112)
         */
        @JsonProperty("tday_rltv")
        private String todayRelativeStrength;

        /**
         * 매도 체결량 합계 (String, 18)
         */
        @JsonProperty("seln_cnqn_smtn")
        private String sellExecutionVolumeSum;

        /**
         * 매수2 체결량 합계 (String, 18)
         */
        @JsonProperty("shnu_cnqn_smtn")
        private String buyExecutionVolumeSum;
    }
}
