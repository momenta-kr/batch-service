package com.hyunha.batch.stock.call_kis_api_job.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StocksOfInterestRankResponse {

    /**
     * 응답상세 (Object Array)
     */
    @JsonProperty("output")
    private List<Output> output;

    /**
     * output 배열 요소
     */
    @Data
    public static class Output {

        /**
         * 시장 분류 구분 명 (String, 40)
         */
        @JsonProperty("mrkt_div_cls_name")
        private String marketDivisionClassName;

        /**
         * 유가증권 단축 종목코드 (String, 9)
         */
        @JsonProperty("mksc_shrn_iscd")
        private String shortSymbolCode;

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
         * 누적 거래 대금 (String, 18)
         */
        @JsonProperty("acml_tr_pbmn")
        private String accumulatedTradeAmount;

        /**
         * 매도호가 (String, 10)
         */
        @JsonProperty("askp")
        private String askPrice;

        /**
         * 매수호가 (String, 10)
         */
        @JsonProperty("bidp")
        private String bidPrice;

        /**
         * 데이터 순위 (String, 10)
         */
        @JsonProperty("data_rank")
        private String dataRank;

        /**
         * 관심 종목 등록 건수 (String, 10)
         */
        @JsonProperty("inter_issu_reg_csnu")
        private String interestIssueRegisterCount;
    }
}
