package com.hyunha.batch.stock.call_kis_api_job.application;

import com.hyunha.batch.stock.call_kis_api_job.domain.ScoredSymbol;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.StockTierDailyRepository;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.StockTierDaily;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.Tier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TierDailyPersistenceService {

    private final StockTierDailyRepository stockTierDailyRepository;

    public record TierCut(int t0, int t1, int t2) {
        public int totalTop() { return t0 + t1 + t2; }
    }

    // 너가 말한 기준
    private static final TierCut DEFAULT_CUT = new TierCut(120, 180, 350); // T3 = 나머지

    @Transactional
    public SaveResult saveDaily(LocalDate asOfDate, List<ScoredSymbol> scoredSymbolsDesc) {
        if (scoredSymbolsDesc == null || scoredSymbolsDesc.isEmpty()) {
            throw new IllegalArgumentException("scoredSymbolsDesc is empty");
        }

        // ✅ 954개를 기대(더 많아도 상관없고, 적으면 T3가 줄어듦)
        // 필요하면 여기서 size 체크도 가능
        int deleted = stockTierDailyRepository.deleteByAsOfDate(asOfDate);

        Instant now = Instant.now();
        List<StockTierDaily> rows = new ArrayList<>(scoredSymbolsDesc.size());

        int t0End = Math.min(DEFAULT_CUT.t0(), scoredSymbolsDesc.size());
        int t1End = Math.min(DEFAULT_CUT.t0() + DEFAULT_CUT.t1(), scoredSymbolsDesc.size());
        int t2End = Math.min(DEFAULT_CUT.t0() + DEFAULT_CUT.t1() + DEFAULT_CUT.t2(), scoredSymbolsDesc.size());

        for (int i = 0; i < scoredSymbolsDesc.size(); i++) {
            ScoredSymbol s = scoredSymbolsDesc.get(i);

            Tier tier;
            if (i < t0End) tier = Tier.T0;
            else if (i < t1End) tier = Tier.T1;
            else if (i < t2End) tier = Tier.T2;
            else tier = Tier.T3;

            rows.add(StockTierDaily.builder()
                    .asOfDate(asOfDate)
                    .symbol(s.symbol())   // 종목코드
                    .tier(tier)           // "T0"~"T3"
                    .score(s.score())     // 점수
                    .updatedAt(now)       // 한번에 고정
                    .build());
        }

        stockTierDailyRepository.saveAll(rows);

        int t0 = t0End;
        int t1 = Math.max(0, t1End - t0End);
        int t2 = Math.max(0, t2End - t1End);
        int t3 = Math.max(0, scoredSymbolsDesc.size() - t2End);

        return new SaveResult(asOfDate, deleted, rows.size(), t0, t1, t2, t3);
    }

    public record SaveResult(
            LocalDate asOfDate,
            int deletedRows,
            int insertedRows,
            int t0Count,
            int t1Count,
            int t2Count,
            int t3Count
    ) {}
}
