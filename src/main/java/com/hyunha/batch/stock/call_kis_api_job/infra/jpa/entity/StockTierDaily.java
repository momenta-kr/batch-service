package com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "stock_tier_daily",
        indexes = {
                @Index(name = "idx_stock_tier_daily_tier", columnList = "as_of_date,tier")
        }
)
public class StockTierDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL bigserial
    @Column(name = "id", nullable = false)
    private Long id;

    /** 기준일자 (YYYY-MM-DD) */
    @Column(name = "as_of_date", nullable = false)
    private LocalDate asOfDate;

    /** 종목코드 (예: 005930) */
    @Column(name = "symbol", nullable = false, columnDefinition = "text")
    private String symbol;

    /** 티어 (예: T0/T1/T2/T3) */
    @Column(name = "tier", nullable = false, columnDefinition = "text")
    @Enumerated(EnumType.STRING)
    private Tier tier;

    /** 점수 */
    @Column(name = "score", nullable = false)
    private double score;

    /** 갱신 시각 */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
