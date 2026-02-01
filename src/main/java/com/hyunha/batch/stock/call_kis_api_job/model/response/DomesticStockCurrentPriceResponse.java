package com.hyunha.batch.stock.call_kis_api_job.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * [국내주식] 주식현재가 시세 (v1_국내주식-008)
 * - URL: /uapi/domestic-stock/v1/quotations/inquire-price
 * - TR_ID: FHKST01010100
 *
 * ⚠️ 한국투자증권 OpenAPI는 숫자/금액도 문자열(string)로 내려주는 필드가 많습니다.
 *    (원하면 BigDecimal/Long로 파싱하는 별도 DTO/Mapper도 만들어줄게)
 */
@Data
public class DomesticStockCurrentPriceResponse extends BaseResponse {

    /** 응답 상세 (JSON: output) */
    @JsonProperty("output")
    private Output output;

    @Data
    public static class Output {

        /**
         * 종목 상태 구분 코드 (JSON: iscd_stat_cls_code)
         * - 51: 관리종목, 52: 투자위험, 53: 투자경고, 54: 투자주의
         * - 55: 신용가능, 57: 증거금 100%, 58: 거래정지, 59: 단기과열종목
         */
        @JsonProperty("iscd_stat_cls_code")
        private String stockStatusClassificationCode;

        /** 증거금 비율 (JSON: marg_rate) */
        @JsonProperty("marg_rate")
        private String marginRate;

        /** 대표 시장 한글 명 (JSON: rprs_mrkt_kor_name) */
        @JsonProperty("rprs_mrkt_kor_name")
        private String representativeMarketKoreanName;

        /** 신 고가/저가 구분 코드 (JSON: new_hgpr_lwpr_cls_code) */
        @JsonProperty("new_hgpr_lwpr_cls_code")
        private String newHighLowClassificationCode;

        /** 업종 한글 종목명 (JSON: bstp_kor_isnm) */
        @JsonProperty("bstp_kor_isnm")
        private String industryKoreanName;

        /** 임시 정지 여부 (Y/N) (JSON: temp_stop_yn) */
        @JsonProperty("temp_stop_yn")
        private String temporaryStopYn;

        /** 시가 범위 연장 여부 (Y/N) (JSON: oprc_rang_cont_yn) */
        @JsonProperty("oprc_rang_cont_yn")
        private String openPriceRangeExtensionYn;

        /** 종가 범위 연장 여부 (Y/N) (JSON: clpr_rang_cont_yn) */
        @JsonProperty("clpr_rang_cont_yn")
        private String closePriceRangeExtensionYn;

        /** 신용 가능 여부 (Y/N) (JSON: crdt_able_yn) */
        @JsonProperty("crdt_able_yn")
        private String creditAvailableYn;

        /** 보증금 비율 구분 코드 (JSON: grmn_rate_cls_code) */
        @JsonProperty("grmn_rate_cls_code")
        private String guaranteeDepositRateClassificationCode;

        /** ELW 발행 여부 (Y/N) (JSON: elw_pblc_yn) */
        @JsonProperty("elw_pblc_yn")
        private String elwIssuedYn;

        /** 주식 현재가 (JSON: stck_prpr) */
        @JsonProperty("stck_prpr")
        private String currentPrice;

        /** 전일 대비 (JSON: prdy_vrss) */
        @JsonProperty("prdy_vrss")
        private String changeFromPreviousDay;

        /** 전일 대비 부호 (JSON: prdy_vrss_sign) */
        @JsonProperty("prdy_vrss_sign")
        private String changeSignFromPreviousDay;

        /** 전일 대비율 (JSON: prdy_ctrt) */
        @JsonProperty("prdy_ctrt")
        private String changeRateFromPreviousDay;

        /** 누적 거래 대금 (JSON: acml_tr_pbmn) */
        @JsonProperty("acml_tr_pbmn")
        private String accumulatedTradeAmount;

        /** 누적 거래량 (JSON: acml_vol) */
        @JsonProperty("acml_vol")
        private String accumulatedVolume;

        /** 전일 대비 거래량 비율 (JSON: prdy_vrss_vol_rate) */
        @JsonProperty("prdy_vrss_vol_rate")
        private String volumeChangeRateFromPreviousDay;

        /** 주식 시가 (문서 표기: 주식 시가2) (JSON: stck_oprc) */
        @JsonProperty("stck_oprc")
        private String openPrice;

        /** 주식 최고가 (JSON: stck_hgpr) */
        @JsonProperty("stck_hgpr")
        private String highPrice;

        /** 주식 최저가 (JSON: stck_lwpr) */
        @JsonProperty("stck_lwpr")
        private String lowPrice;

        /** 주식 상한가 (JSON: stck_mxpr) */
        @JsonProperty("stck_mxpr")
        private String upperLimitPrice;

        /** 주식 하한가 (JSON: stck_llam) */
        @JsonProperty("stck_llam")
        private String lowerLimitPrice;

        /** 주식 기준가 (JSON: stck_sdpr) */
        @JsonProperty("stck_sdpr")
        private String basePrice;

        /** 가중 평균 주식 가격 (JSON: wghn_avrg_stck_prc) */
        @JsonProperty("wghn_avrg_stck_prc")
        private String weightedAveragePrice;

        /** HTS 외국인 소진율 (JSON: hts_frgn_ehrt) */
        @JsonProperty("hts_frgn_ehrt")
        private String htsForeignExhaustionRate;

        /** 외국인 순매수 수량 (JSON: frgn_ntby_qty) */
        @JsonProperty("frgn_ntby_qty")
        private String foreignNetBuyQuantity;

        /** 프로그램매매 순매수 수량 (JSON: pgtr_ntby_qty) */
        @JsonProperty("pgtr_ntby_qty")
        private String programNetBuyQuantity;

        /** 피벗 2차 디저항 가격 (JSON: pvt_scnd_dmrs_prc) */
        @JsonProperty("pvt_scnd_dmrs_prc")
        private String pivotSecondResistancePrice;

        /** 피벗 1차 디저항 가격 (JSON: pvt_frst_dmrs_prc) */
        @JsonProperty("pvt_frst_dmrs_prc")
        private String pivotFirstResistancePrice;

        /** 피벗 포인트 값 (JSON: pvt_pont_val) */
        @JsonProperty("pvt_pont_val")
        private String pivotPointValue;

        /** 피벗 1차 디지지 가격 (JSON: pvt_frst_dmsp_prc) */
        @JsonProperty("pvt_frst_dmsp_prc")
        private String pivotFirstSupportPrice;

        /** 피벗 2차 디지지 가격 (JSON: pvt_scnd_dmsp_prc) */
        @JsonProperty("pvt_scnd_dmsp_prc")
        private String pivotSecondSupportPrice;

        /** 디저항 값 (JSON: dmrs_val) */
        @JsonProperty("dmrs_val")
        private String resistanceValue;

        /** 디지지 값 (JSON: dmsp_val) */
        @JsonProperty("dmsp_val")
        private String supportValue;

        /** 자본금 (JSON: cpfn) */
        @JsonProperty("cpfn")
        private String capitalAmount;

        /** 제한 폭 가격 (JSON: rstc_wdth_prc) */
        @JsonProperty("rstc_wdth_prc")
        private String priceLimitWidth;

        /** 주식 액면가 (JSON: stck_fcam) */
        @JsonProperty("stck_fcam")
        private String faceValue;

        /** 주식 대용가 (JSON: stck_sspr) */
        @JsonProperty("stck_sspr")
        private String substitutePrice;

        /** 호가단위 (JSON: aspr_unit) */
        @JsonProperty("aspr_unit")
        private String quoteUnit;

        /** HTS 매매 수량 단위 값 (JSON: hts_deal_qty_unit_val) */
        @JsonProperty("hts_deal_qty_unit_val")
        private String htsTradeQuantityUnitValue;

        /** 상장 주수 (JSON: lstn_stcn) */
        @JsonProperty("lstn_stcn")
        private String listedShares;

        /** HTS 시가총액 (JSON: hts_avls) */
        @JsonProperty("hts_avls")
        private String htsMarketCap;

        /** PER (JSON: per) */
        @JsonProperty("per")
        private String per;

        /** PBR (JSON: pbr) */
        @JsonProperty("pbr")
        private String pbr;

        /** 결산 월 (JSON: stac_month) */
        @JsonProperty("stac_month")
        private String settlementMonth;

        /** 거래량 회전율 (JSON: vol_tnrt) */
        @JsonProperty("vol_tnrt")
        private String volumeTurnoverRate;

        /** EPS (JSON: eps) */
        @JsonProperty("eps")
        private String eps;

        /** BPS (JSON: bps) */
        @JsonProperty("bps")
        private String bps;

        /** 250일 최고가 (JSON: d250_hgpr) */
        @JsonProperty("d250_hgpr")
        private String day250HighPrice;

        /** 250일 최고가 일자 (JSON: d250_hgpr_date) */
        @JsonProperty("d250_hgpr_date")
        private String day250HighPriceDate;

        /** 250일 최고가 대비 현재가 비율 (JSON: d250_hgpr_vrss_prpr_rate) */
        @JsonProperty("d250_hgpr_vrss_prpr_rate")
        private String day250HighPriceVsCurrentRate;

        /** 250일 최저가 (JSON: d250_lwpr) */
        @JsonProperty("d250_lwpr")
        private String day250LowPrice;

        /** 250일 최저가 일자 (JSON: d250_lwpr_date) */
        @JsonProperty("d250_lwpr_date")
        private String day250LowPriceDate;

        /** 250일 최저가 대비 현재가 비율 (JSON: d250_lwpr_vrss_prpr_rate) */
        @JsonProperty("d250_lwpr_vrss_prpr_rate")
        private String day250LowPriceVsCurrentRate;

        /** 주식 연중 최고가 (JSON: stck_dryy_hgpr) */
        @JsonProperty("stck_dryy_hgpr")
        private String yearlyHighPrice;

        /** 연중 최고가 대비 현재가 비율 (JSON: dryy_hgpr_vrss_prpr_rate) */
        @JsonProperty("dryy_hgpr_vrss_prpr_rate")
        private String yearlyHighPriceVsCurrentRate;

        /** 연중 최고가 일자 (JSON: dryy_hgpr_date) */
        @JsonProperty("dryy_hgpr_date")
        private String yearlyHighPriceDate;

        /** 주식 연중 최저가 (JSON: stck_dryy_lwpr) */
        @JsonProperty("stck_dryy_lwpr")
        private String yearlyLowPrice;

        /** 연중 최저가 대비 현재가 비율 (JSON: dryy_lwpr_vrss_prpr_rate) */
        @JsonProperty("dryy_lwpr_vrss_prpr_rate")
        private String yearlyLowPriceVsCurrentRate;

        /** 연중 최저가 일자 (JSON: dryy_lwpr_date) */
        @JsonProperty("dryy_lwpr_date")
        private String yearlyLowPriceDate;

        /** 52주일 최고가 (JSON: w52_hgpr) */
        @JsonProperty("w52_hgpr")
        private String week52HighPrice;

        /** 52주일 최고가 대비 현재가 대비 (JSON: w52_hgpr_vrss_prpr_ctrt) */
        @JsonProperty("w52_hgpr_vrss_prpr_ctrt")
        private String week52HighPriceVsCurrentRate;

        /** 52주일 최고가 일자 (JSON: w52_hgpr_date) */
        @JsonProperty("w52_hgpr_date")
        private String week52HighPriceDate;

        /** 52주일 최저가 (JSON: w52_lwpr) */
        @JsonProperty("w52_lwpr")
        private String week52LowPrice;

        /** 52주일 최저가 대비 현재가 대비 (JSON: w52_lwpr_vrss_prpr_ctrt) */
        @JsonProperty("w52_lwpr_vrss_prpr_ctrt")
        private String week52LowPriceVsCurrentRate;

        /** 52주일 최저가 일자 (JSON: w52_lwpr_date) */
        @JsonProperty("w52_lwpr_date")
        private String week52LowPriceDate;

        /** 전체 융자 잔고 비율 (JSON: whol_loan_rmnd_rate) */
        @JsonProperty("whol_loan_rmnd_rate")
        private String totalLoanBalanceRate;

        /** 공매도 가능 여부 (Y/N) (JSON: ssts_yn) */
        @JsonProperty("ssts_yn")
        private String shortSellingAvailableYn;

        /** 주식 단축 종목코드 (JSON: stck_shrn_iscd) */
        @JsonProperty("stck_shrn_iscd")
        private String stockShortCode;

        /** 액면가 통화명 (JSON: fcam_cnnm) */
        @JsonProperty("fcam_cnnm")
        private String faceValueCurrencyName;

        /** 자본금 통화명 (JSON: cpfn_cnnm) */
        @JsonProperty("cpfn_cnnm")
        private String capitalCurrencyName;

        /** 접근도 (JSON: apprch_rate) */
        @JsonProperty("apprch_rate")
        private String approachRate;

        /** 외국인 보유 수량 (JSON: frgn_hldn_qty) */
        @JsonProperty("frgn_hldn_qty")
        private String foreignHoldingQuantity;

        /** VI 적용 구분 코드 (JSON: vi_cls_code) */
        @JsonProperty("vi_cls_code")
        private String viAppliedClassificationCode;

        /** 시간외 단일가 VI 적용 구분 코드 (JSON: ovtm_vi_cls_code) */
        @JsonProperty("ovtm_vi_cls_code")
        private String afterHoursViAppliedClassificationCode;

        /** 최종 공매도 체결 수량 (JSON: last_ssts_cntg_qty) */
        @JsonProperty("last_ssts_cntg_qty")
        private String lastShortSellingContractQuantity;

        /** 투자유의 여부 (Y/N) (JSON: invt_caful_yn) */
        @JsonProperty("invt_caful_yn")
        private String investmentCautionYn;

        /** 시장경고 코드 (JSON: mrkt_warn_cls_code) */
        @JsonProperty("mrkt_warn_cls_code")
        private String marketWarningCode;

        /** 단기과열 여부 (Y/N) (JSON: short_over_yn) */
        @JsonProperty("short_over_yn")
        private String shortTermOverheatedYn;

        /** 정리매매 여부 (Y/N) (JSON: sltr_yn) */
        @JsonProperty("sltr_yn")
        private String liquidationTradeYn;

        /** 관리종목 여부 (Y/N) (JSON: mang_issu_cls_code) */
        @JsonProperty("mang_issu_cls_code")
        private String managedStockYn;
    }
}
