package com.hyunha.batch.stock.call_kis_api_job.infra.jpa;

import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.StockTierDaily;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.Tier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockTierDailyRepository extends JpaRepository<StockTierDaily, Long> {

    int deleteByAsOfDate(LocalDate asOfDate);

    List<StockTierDaily> findByAsOfDateAndTierOrderByScore(LocalDate asOfDate, Tier tier);
}
