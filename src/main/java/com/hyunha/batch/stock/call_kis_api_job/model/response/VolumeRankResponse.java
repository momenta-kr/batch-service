package com.hyunha.batch.stock.call_kis_api_job.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VolumeRankResponse extends BaseResponse {

    /**
     * 응답상세 (Object Array)
     */
    @JsonProperty("output")
    private List<Output> outputList;

    /**
     * Output 배열 요소
     */
    @Data
    public static class Output {

        /**
         * HTS 한글 종목명 (String, 40)
         */
        @JsonProperty("hts_kor_isnm")
        private String htsKoreanName;

        /**
         * 유가증권 단축 종목코드 (String, 9)
         */
        @JsonProperty("mksc_shrn_iscd")
        private String shortSymbolCode;

        /**
         * 데이터 순위 (String, 10)
         */
        @JsonProperty("data_rank")
        private String dataRank;

        /**
         * 주식 현재가 (String, 10)
         */
        @JsonProperty("stck_prpr")
        private String currentPrice;

        /**
         * 전일 대비 부호 (String, 1)
         * - 상승/하락/보합 등 코드 (정확한 의미는 KIS 정의에 따름)
         */
        @JsonProperty("prdy_vrss_sign")
        private String prevDayDiffSign;

        /**
         * 전일 대비 (String, 10)
         */
        @JsonProperty("prdy_vrss")
        private String prevDayDiff;

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
         * 전일 거래량 (String, 18)
         */
        @JsonProperty("prdy_vol")
        private String prevDayVolume;

        /**
         * 상장 주수 (String, 18)
         */
        @JsonProperty("lstn_stcn")
        private String listedShares;

        /**
         * 평균 거래량 (String, 18)
         */
        @JsonProperty("avrg_vol")
        private String averageVolume;

        /**
         * N일전종가대비현재가대비율 (String, 82)
         */
        @JsonProperty("n_befr_clpr_vrss_prpr_rate")
        private String nDaysBeforeCloseVsCurrentRate;

        /**
         * 거래량증가율 (String, 84)
         */
        @JsonProperty("vol_inrt")
        private String volumeIncreaseRate;

        /**
         * 거래량 회전율 (String, 82)
         */
        @JsonProperty("vol_tnrt")
        private String volumeTurnoverRate;

        /**
         * N일 거래량 회전율 (String, 8)
         */
        @JsonProperty("nday_vol_tnrt")
        private String nDayVolumeTurnoverRate;

        /**
         * 평균 거래 대금 (String, 18)
         */
        @JsonProperty("avrg_tr_pbmn")
        private String averageTradeAmount;

        /**
         * 거래대금회전율 (String, 82)
         */
        @JsonProperty("tr_pbmn_tnrt")
        private String tradeAmountTurnoverRate;

        /**
         * N일 거래대금 회전율 (String, 8)
         */
        @JsonProperty("nday_tr_pbmn_tnrt")
        private String nDayTradeAmountTurnoverRate;

        /**
         * 누적 거래 대금 (String, 18)
         */
        @JsonProperty("acml_tr_pbmn")
        private String accumulatedTradeAmount;
    }
}
