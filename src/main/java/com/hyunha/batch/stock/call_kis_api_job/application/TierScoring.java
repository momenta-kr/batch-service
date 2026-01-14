package com.hyunha.batch.stock.call_kis_api_job.application;

import java.util.*;

public final class TierScoring {

    private TierScoring() {}

    /** rank(1..N) -> score(0..1], 1등=1.0 */
    public static double rankToScore(int rank, double alpha) {
        if (rank <= 0) return 0.0;
        return 1.0 / Math.pow(rank, alpha);
    }

    public enum Source {
        MARKET_CAP(1.2),
        VOLUME(1.1),
        INTEREST(0.9),
        TRADE_STRENGTH(1.0),
        GAINERS(0.7),
        LOSERS(0.7);

        public final double weight;
        Source(double weight) { this.weight = weight; }
    }

    /** 전 종목 점수 계산 */
    public static Map<String, Double> computeScores(
            List<String> universeOrdered,
            Map<Source, Map<String, Integer>> ranksBySource,
            double alpha
    ) {
        Map<String, Double> scoreBySymbol = new HashMap<>(universeOrdered.size() * 2);

        for (String sym0 : universeOrdered) {
            String sym = RankUtils.normalizeSymbol(sym0);
            double sum = 0.0;

            for (var e : ranksBySource.entrySet()) {
                Source src = e.getKey();
                Map<String, Integer> rankMap = e.getValue();
                int rank = rankMap.getOrDefault(sym, universeOrdered.size()); // 안전 fallback
                sum += src.weight * rankToScore(rank, alpha);
            }

            scoreBySymbol.put(sym, sum);
        }
        return scoreBySymbol;
    }
}
