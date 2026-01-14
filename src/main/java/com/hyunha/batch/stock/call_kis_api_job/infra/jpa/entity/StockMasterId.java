package com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StockMasterId implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(name = "market", nullable = false)
    private StockMarket market;


    @Column(name = "symbol", nullable = false)
    private String symbol;
}