package com.hyunha.batch.stock.call_kis_api_job.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class MarketCapQuery {

    /**
     * 입력 가격2 (String, 12)
     * - 입력값 없을 때: 전체 (~ 가격)
     * - 예: 최대가격 필터(upper bound)
     */
    @JsonProperty("fid_input_price_2")
    private String maxPrice;

    /**
     * 조건 시장 분류 코드 (String, 2)
     * - 시장구분코드
     *   J  : KRX
     *   NX : NXT
     */
    @JsonProperty("fid_cond_mrkt_div_code")
    private String marketDivisionCode;

    /**
     * 조건 화면 분류 코드 (String, 5)
     * - Unique key (예: 20174)
     * - 보통 TR별로 고정값 사용
     */
    @JsonProperty("fid_cond_scr_div_code")
    private String screenDivisionCode;

    /**
     * 분류 구분 코드 (String, 2)
     * - 0: 전체
     * - 1: 보통주
     * - 2: 우선주
     */
    @JsonProperty("fid_div_cls_code")
    private String shareClassCode;

    /**
     * 입력 종목코드 (String, 12)
     * - 0000: 전체
     * - 0001: 거래소
     * - 1001: 코스닥
     * - 2001: 코스피200
     *
     * ※ 필드명은 'input stock code'지만 실제 의미는 "대상 시장/그룹 코드"에 가깝다.
     */
    @JsonProperty("fid_input_iscd")
    private String targetMarketOrGroupCode;

    /**
     * 대상 구분 코드 (String, 32)
     * - 0: 전체
     * - (확장 가능: 특정 대상군 필터)
     */
    @JsonProperty("fid_trgt_cls_code")
    private String targetClassCode;

    /**
     * 대상 제외 구분 코드 (String, 32)
     * - 0: 전체
     * - (확장 가능: 특정 대상군 제외 필터)
     */
    @JsonProperty("fid_trgt_exls_cls_code")
    private String targetExcludeClassCode;

    /**
     * 입력 가격1 (String, 12)
     * - 입력값 없을 때: 전체 (가격 ~)
     * - 예: 최소가격 필터(lower bound)
     */
    @JsonProperty("fid_input_price_1")
    private String minPrice;

    /**
     * 거래량 수 (String, 12)
     * - 입력값 없을 때: 전체 (거래량 ~)
     * - 예: 최소 거래량 필터(lower bound)
     */
    @JsonProperty("fid_vol_cnt")
    private String minVolume;
}
