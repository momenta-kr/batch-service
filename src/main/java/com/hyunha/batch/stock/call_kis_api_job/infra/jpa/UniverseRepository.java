package com.hyunha.batch.stock.call_kis_api_job.infra.jpa;

import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.Universe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UniverseRepository extends JpaRepository<Universe, Universe.UniverseId> {

    @Query(value = "SELECT * FROM stock_master WHERE market = 'KOSPI' AND sector_l != '0' ORDER BY market_cap desc", nativeQuery = true)
    List<Universe> findKospiByMarketCapDesc();
}
