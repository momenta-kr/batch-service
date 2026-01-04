package com.hyunha.batch.stock.call_kis_api_job.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MarketCapRankResponse extends BaseResponse{

    @JsonProperty("output")
    private List<Output> outputList;

    @Data
    public static class Output {

        /**
         * 유가증권 단축 종목코드 (String, 9)
         * - 예: "005930"
         */
        @JsonProperty("mksc_shrn_iscd")
        private String shortSymbolCode;

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
         * - 예: 상승/하락/보합 코드 (KIS 정의에 따름)
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
         * 상장 주수 (String, 18)
         */
        @JsonProperty("lstn_stcn")
        private String listedShares;

        /**
         * 시가 총액 (String, 18)
         */
        @JsonProperty("stck_avls")
        private String marketCap;

        /**
         * 시장 전체 시가총액 비중 (String, 52)
         * - 예: KOSPI 전체 대비 비중 등 (정확한 의미는 TR 설명에 따름)
         */
        @JsonProperty("mrkt_whol_avls_rlim")
        private String marketTotalMarketCapRatio;
    }
}
