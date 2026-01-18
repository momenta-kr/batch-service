package com.hyunha.batch.stock.call_kis_api_job.tasklet;

import com.hyunha.batch.stock.call_kis_api_job.application.RankingClient;
import com.hyunha.batch.stock.call_kis_api_job.application.TierDailyPersistenceService;
import com.hyunha.batch.stock.call_kis_api_job.application.TierScoring;
import com.hyunha.batch.stock.call_kis_api_job.domain.ScoredSymbol;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.UniverseRepository;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.Universe;
import com.hyunha.batch.stock.call_kis_api_job.model.response.MarketCapRankResponse;
import com.hyunha.batch.stock.call_kis_api_job.model.response.VolumeRankResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.hyunha.batch.stock.call_kis_api_job.application.RankUtils.buildFullRankMap;

@Transactional
@RequiredArgsConstructor
@Component
public class FetchVariousRankingForTierTasklet implements Tasklet {

    private final RankingClient kisRankingClient;
    private final UniverseRepository universeRepository;
    private final TierDailyPersistenceService tierDailyPersistenceService;

    @Nullable
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        var marketCap = kisRankingClient.fetchMarketCapRanking();
        var volume    = kisRankingClient.fetchVolumeRanking();
        var gainers   = kisRankingClient.fetchTopGainers();
        var losers    = kisRankingClient.fetchTopLosers();
        var interest  = kisRankingClient.fetchStocksOfInterestRanking();
        var strength  = kisRankingClient.fetchTradeStrengthRanking();

        List<String> universeOrdered = universeRepository
                .findKospiByMarketCapDesc()
                .stream().map(Universe::getSymbol).toList();

        // ✅ 소스별 rankMap(954개 완성)
        Map<TierScoring.Source, Map<String, Integer>> ranksBySource = new EnumMap<>(TierScoring.Source.class);

        ranksBySource.put(
                TierScoring.Source.MARKET_CAP,
                buildFullRankMap(
                        marketCap.getOutputList(),
                        MarketCapRankResponse.Output::getShortSymbolCode,   // <- 네 DTO getter로 교체
                        universeOrdered
                )
        );

        ranksBySource.put(
                TierScoring.Source.VOLUME,
                buildFullRankMap(
                        volume.getOutputList(),
                        VolumeRankResponse.Output::getShortSymbolCode,   // <- DTO getter로 교체
                        universeOrdered
                )
        );

        ranksBySource.put(
                TierScoring.Source.GAINERS,
                buildFullRankMap(
                        gainers.getOutput(),
                        o -> o.getShortStockCode(),
                        universeOrdered
                )
        );

        ranksBySource.put(
                TierScoring.Source.LOSERS,
                buildFullRankMap(
                        losers.getOutput(),
                        o -> o.getShortStockCode(),
                        universeOrdered
                )
        );

        ranksBySource.put(
                TierScoring.Source.INTEREST,
                buildFullRankMap(
                        interest.getOutput(),
                        o -> o.getShortSymbolCode(),
                        universeOrdered
                )
        );

        ranksBySource.put(
                TierScoring.Source.TRADE_STRENGTH,
                buildFullRankMap(
                        strength.getOutputList(),
                        o -> o.getShortStockCode(),
                        universeOrdered
                )
        );

        // ✅ 최종 점수
        double alpha = 0.85; // 튜닝 포인트
        Map<String, Double> scoreBySymbol = TierScoring.computeScores(universeOrdered, ranksBySource, alpha);

        // ✅ scoredSymbolsDesc 만들기 (점수 내림차순)
        List<ScoredSymbol> scoredSymbolsDesc = scoreBySymbol.entrySet().stream()
                .map(e -> new ScoredSymbol(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingDouble(ScoredSymbol::score).reversed())
                .toList();

        // TODO: tierPersistenceService.saveDailyAndCurrent(...)
        var r = tierDailyPersistenceService.saveDaily(LocalDate.now(), scoredSymbolsDesc);
        System.out.printf("[TIER SAVE] date=%s inserted=%d (T0=%d T1=%d T2=%d T3=%d) deleted=%d%n",
                r.asOfDate(), r.insertedRows(), r.t0Count(), r.t1Count(), r.t2Count(), r.t3Count(), r.deletedRows());



        return RepeatStatus.FINISHED;
    }
}
