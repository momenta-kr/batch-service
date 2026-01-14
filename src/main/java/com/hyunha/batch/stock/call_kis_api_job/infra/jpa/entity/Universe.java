package com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Table(
        name = "stock_master",
        indexes = {
                @Index(name = "idx_stock_master_sector", columnList = "market, sector_l, sector_m, sector_s"),
                @Index(name = "idx_stock_master_status", columnList = "market, status"),
                @Index(name = "idx_stock_master_symbol", columnList = "symbol")
        }
)
@Entity
public class Universe {
    @EmbeddedId
    private UniverseId id;

    /** 표준코드 */
    @Column(name = "std_code")
    private String stdCode;

    /** 한글명 */
    @Column(name = "name_ko")
    private String nameKo;

    /** 지수업종 대분류 */
    @Column(name = "sector_l")
    private String sectorL;

    /** 지수업종 중분류 */
    @Column(name = "sector_m")
    private String sectorM;

    /** 지수업종 소분류 */
    @Column(name = "sector_s")
    private String sectorS;

    /** 시가총액 */
    @Column(name = "market_cap")
    private Long marketCap;

    /** 상장주수 */
    @Column(name = "listed_shares")
    private Long listedShares;

    /** 기준가 */
    @Column(name = "price_base")
    private Long priceBase;

    /** 거래정지 여부 */
    @Column(name = "is_trading_halt", nullable = false)
    private Boolean tradingHalt = false;

    /** 정리매매 여부 */
    @Column(name = "is_liquidation", nullable = false)
    private Boolean liquidation = false;

    /** 관리종목 여부 */
    @Column(name = "is_managed", nullable = false)
    private Boolean managed = false;

    /** 시장경고 여부 */
    @Column(name = "is_warning", nullable = false)
    private Boolean warning = false;

    /** 경고예고 여부 */
    @Column(name = "is_watch", nullable = false)
    private Boolean watch = false;

    /** 단기과열 여부 */
    @Column(name = "is_overheat", nullable = false)
    private Boolean overheat = false;

    /** 상태 (ACTIVE / MISSING / DELIST_CANDIDATE / DELISTED) */
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    /** 누락 시작일 */
    @Column(name = "missing_since")
    private LocalDate missingSince;

    /** 상폐 후보 시작일 */
    @Column(name = "candidate_since")
    private LocalDate candidateSince;

    /** 상폐 확정일 */
    @Column(name = "delisted_at")
    private LocalDate delistedAt;

    /** 기준일(as_of_date) */
    @Column(name = "as_of_date", nullable = false)
    private LocalDate asOfDate;

    /** row hash (업데이트 판단용) */
    @Column(name = "row_hash", nullable = false)
    private String rowHash;

    /** 갱신시각 */
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // ---- 편의 Getter ----
    public String getMarket() {
        return id == null ? null : id.getMarket();
    }

    public String getSymbol() {
        return id == null ? null : id.getSymbol();
    }

    /**
     * PK (market, symbol)
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Embeddable
    public static class UniverseId implements Serializable {

        /** 시장(KOSPI/KOSDAQ/...) */
        @Column(name = "market", nullable = false)
        private String market;

        /** 종목코드(단축코드) */
        @Column(name = "symbol", nullable = false)
        private String symbol;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UniverseId that)) return false;
            return java.util.Objects.equals(market, that.market)
                    && java.util.Objects.equals(symbol, that.symbol);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(market, symbol);
        }
    }

}
