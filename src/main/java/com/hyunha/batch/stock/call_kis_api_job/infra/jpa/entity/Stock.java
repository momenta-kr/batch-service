package com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "stock_master",
        indexes = {
                @Index(name = "idx_stock_master_sector", columnList = "market,sector_l,sector_m,sector_s"),
                @Index(name = "idx_stock_master_status", columnList = "market,status"),
                @Index(name = "idx_stock_master_symbol", columnList = "symbol")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @EmbeddedId
    private StockMasterId id;

    @Column(name = "std_code")
    private String stdCode;

    @Column(name = "name_ko")
    private String nameKo;

    @Column(name = "sector_l")
    private String sectorL;

    @Column(name = "sector_m")
    private String sectorM;

    @Column(name = "sector_s")
    private String sectorS;

    @Column(name = "market_cap")
    private Long marketCap;

    @Column(name = "listed_shares")
    private Long listedShares;

    @Column(name = "price_base")
    private Long priceBase;

    @Column(name = "is_trading_halt", nullable = false)
    private boolean tradingHalt = false;

    @Column(name = "is_liquidation", nullable = false)
    private boolean liquidation = false;

    @Column(name = "is_managed", nullable = false)
    private boolean managed = false;

    @Column(name = "is_warning", nullable = false)
    private boolean warning = false;

    @Column(name = "is_watch", nullable = false)
    private boolean watch = false;

    @Column(name = "is_overheat", nullable = false)
    private boolean overheat = false;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false)
    private StockStatus status = StockStatus.ACTIVE;

    @Column(name = "missing_since")
    private LocalDate missingSince;

    @Column(name = "candidate_since")
    private LocalDate candidateSince;

    @Column(name = "delisted_at")
    private LocalDate delistedAt;

    @Column(name = "as_of_date", nullable = false)
    private LocalDate asOfDate;

    @Column(name = "row_hash", nullable = false)
    private String rowHash;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // 편의 getter (원하면 제거)
    public StockMarket getMarket() {
        return id != null ? id.getMarket() : null;
    }

    public String getSymbol() {
        return id != null ? id.getSymbol() : null;
    }

}