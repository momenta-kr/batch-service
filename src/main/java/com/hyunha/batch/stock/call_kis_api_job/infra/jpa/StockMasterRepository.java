package com.hyunha.batch.stock.call_kis_api_job.infra.jpa;

import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.Stock;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.StockMasterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockMasterRepository extends JpaRepository<Stock, StockMasterId> {

    @Query(value = "SELECT * FROM stock_master WHERE market = 'KOSPI' and sector_l != '0'", nativeQuery = true)
    List<Stock> findAllKospi();

    List<Stock> findByIdIn(List<StockMasterId> stockMasterIds);
}
