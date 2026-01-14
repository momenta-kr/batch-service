package com.hyunha.batch.stock.call_kis_api_job.application;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 순위 기반 점수화 + 지표 가중치 + 옵션(추가) 가중치 적용.
 * <p>
 * 개념
 * - 각 지표별 ranking list(1등~N등)를 입력받는다.
 * - rank -> score로 변환 (기본: linear)
 * - finalScore(symbol) = Σ [ metricWeight * metricScore(symbol) * optionWeight(symbol, metric) ]
 * <p>
 * 사용처
 * - 장마감 후 Tier 재계산
 * - 후보군(랭킹 합집합) 점수화
 */
public final class RankScoring {

    private RankScoring() {
    }

    /**
     * 어떤 지표로 점수를 만들지
     */
    public enum Metric {
        /**
         * 거래대금(유동성)
         */
        TRADE_AMOUNT,
        /**
         * 시가총액
         */
        MARKET_CAP,
        /**
         * 관심(인기)
         */
        INTEREST,
        /**
         * 등락률(옵션)
         */
        FLUCTUATION,
        /**
         * 체결강도(옵션)
         */
        TRADE_STRENGTH
    }

    /**
     * 랭킹 아이템의 최소 형태 (rank는 1부터)
     */
    public record RankItem(String symbol, int rank) {
    }

    /**
     * 점수 변환기: rank(1..N) -> score(0..1)
     */
    @FunctionalInterface
    public interface RankToScore {
        double toScore(int rank, int maxRank);
    }

    /**
     * 옵션 가중치(추가 곱)
     * 예) KOSPI만 1.05, 거래정지 종목 0.0, 우선주 0.7, 특정 섹터 1.1 등
     * <p>
     * - metric별로 다르게 주고 싶으면 metric을 함께 받는 형태로 둔다.
     */
    @FunctionalInterface
    public interface OptionWeight {
        double weight(String symbol, Metric metric);
    }

    /**
     * 결과: 심볼별 점수 상세
     */
    public record ScoreResult(
            String symbol,
            double totalScore,
            Map<Metric, Double> metricScore,   // metric별 최종 기여 점수(가중치/옵션 포함)
            Map<Metric, Integer> metricRank    // metric별 rank(없으면 null)
    ) {
    }

    /**
     * 기본 rank->score: 선형 (1등=1.0, N등=1/N 정도가 아니라 정확히 1.0~1/N?)
     */
    public static final RankToScore LINEAR = (rank, maxRank) -> {
        if (maxRank <= 0) return 0.0;
        if (rank <= 0) return 0.0;
        if (rank > maxRank) return 0.0;
        // 1등=1.0, maxRank=0.0에 가깝게 선형 하강
        return (maxRank - rank + 1) / (double) maxRank;
    };

    /**
     * 조금 더 상위에 몰아주는 변환(상위권 강조)
     * - power>1이면 상위권 점수가 더 커짐
     */
    public static RankToScore power(double power) {
        return (rank, maxRank) -> Math.pow(LINEAR.toScore(rank, maxRank), power);
    }

    /**
     * 랭킹 리스트를 "symbol -> rank" 맵으로 만든다.
     * - symbol 중복(동일 종목이 여러 번) 있으면 가장 좋은 rank(작은 값)를 사용
     * - rank가 0이거나 음수면 무시
     */
    public static Map<String, Integer> toRankMap(List<RankItem> items) {
        if (items == null || items.isEmpty()) return Map.of();
        Map<String, Integer> map = new HashMap<>();
        for (RankItem it : items) {
            if (it == null) continue;
            String s = normalizeSymbol(it.symbol);
            int r = it.rank;
            if (s == null || s.isBlank() || r <= 0) continue;

            map.merge(s, r, Math::min);
        }
        return map;
    }

    /**
     * 심볼 정규화(6자리) - 필요없으면 너 스타일로 바꿔도 됨
     */
    public static String normalizeSymbol(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("\\D", "");
        if (digits.isEmpty()) return raw.trim();
        return String.format("%06d", Integer.parseInt(digits));
    }

    /**
     * 핵심 API:
     * - rankings: metric -> 랭킹 리스트
     * - metricWeights: metric -> 가중치 (합 1일 필요 없음)
     * - optionWeight: 추가 가중치(없으면 1.0)
     * - rankToScore: rank -> score 변환기
     */
    public static List<ScoreResult> score(
            Map<Metric, List<RankItem>> rankings,
            Map<Metric, Double> metricWeights,
            OptionWeight optionWeight,
            RankToScore rankToScore
    ) {
        Objects.requireNonNull(rankings, "rankings");
        Objects.requireNonNull(metricWeights, "metricWeights");
        optionWeight = (optionWeight == null) ? ((s, m) -> 1.0) : optionWeight;
        rankToScore = (rankToScore == null) ? LINEAR : rankToScore;

        // metric별 rankMap, maxRank 준비
        Map<Metric, Map<String, Integer>> rankMaps = new EnumMap<>(Metric.class);
        Map<Metric, Integer> maxRanks = new EnumMap<>(Metric.class);

        for (var e : rankings.entrySet()) {
            Metric metric = e.getKey();
            List<RankItem> list = e.getValue();
            Map<String, Integer> rm = toRankMap(list);
            rankMaps.put(metric, rm);

            int maxRank = 0;
            for (Integer r : rm.values()) {
                if (r != null && r > maxRank) maxRank = r;
            }
            // 보통 랭킹은 1..N이므로 rm.size()가 maxRank와 동일하지 않을 수 있음(동점/중복)
            // maxRank가 0이면 score는 0 처리됨.
            maxRanks.put(metric, maxRank);
        }

        // 후보군(symbol) = 모든 랭킹의 합집합
        Set<String> universe = new HashSet<>();
        for (Map<String, Integer> rm : rankMaps.values()) {
            universe.addAll(rm.keySet());
        }

        // symbol별 점수 계산
        List<ScoreResult> results = new ArrayList<>(universe.size());

        for (String symbol : universe) {
            double total = 0.0;

            Map<Metric, Double> contrib = new EnumMap<>(Metric.class);
            Map<Metric, Integer> usedRanks = new EnumMap<>(Metric.class);

            for (Metric metric : Metric.values()) {
                double w = metricWeights.getOrDefault(metric, 0.0);
                if (w == 0.0) continue;

                Map<String, Integer> rm = rankMaps.getOrDefault(metric, Map.of());
                Integer rank = rm.get(symbol);
                if (rank == null) continue;

                int maxRank = maxRanks.getOrDefault(metric, 0);
                double baseScore = rankToScore.toScore(rank, maxRank); // 0..1
                if (baseScore <= 0.0) continue;

                double opt = optionWeight.weight(symbol, metric);
                if (opt <= 0.0) continue;

                double metricContribution = w * baseScore * opt;

                total += metricContribution;
                contrib.put(metric, metricContribution);
                usedRanks.put(metric, rank);
            }

            results.add(new ScoreResult(symbol, total, contrib, usedRanks));
        }

        // 점수 내림차순 정렬
        results.sort(Comparator.comparingDouble(ScoreResult::totalScore).reversed());
        return results;
    }

    // -----------------------------
    // 예시: 운영용 세팅
    // -----------------------------

    /**
     * 추천 기본 가중치(너가 말한 운영 목표 기반):
     * - 유동성(거래대금) 0.55
     * - 시총 0.25
     * - 관심 0.20
     * - 옵션 지표들은 필요할 때만 추가
     */
    public static Map<Metric, Double> defaultWeights() {
        Map<Metric, Double> w = new EnumMap<>(Metric.class);
        w.put(Metric.TRADE_AMOUNT, 0.45);
        w.put(Metric.MARKET_CAP, 0.20);
        w.put(Metric.INTEREST, 0.15);
        w.put(Metric.FLUCTUATION, 0.10);
        w.put(Metric.TRADE_STRENGTH, 0.10);
        return w;
    }

    /**
     * 예시 옵션 가중치:
     * - 거래정지/정리매매/관리종목 등은 0.0으로 컷
     * - 우선주면 0.7
     * - KOSPI200이면 1.05
     * <p>
     * 실제 운영에서는 DB/캐시에서 종목 속성 읽어오면 됨.
     */
    public static OptionWeight exampleOptionWeight(Function<String, StockAttrs> attrsProvider) {
        return (symbol, metric) -> {
            StockAttrs a = attrsProvider.apply(symbol);
            if (a == null) return 1.0;

            if (a.tradingHalt || a.liquidation || a.managed) return 0.0;

            double w = 1.0;

            if (a.preferredStock) w *= 0.7;
            if (a.kospi200) w *= 1.05;

            // 예: 체결강도는 장중에만 조금 더 의미 있으니까, 마감 후엔 영향 낮추기
            if (metric == Metric.TRADE_STRENGTH) w *= 0.8;

            return w;
        };
    }

    /**
     * 옵션가중치 계산에 필요한 최소 속성 예시
     */
    public static final class StockAttrs {
        public final boolean tradingHalt;     // 거래정지
        public final boolean liquidation;     // 정리매매
        public final boolean managed;         // 관리종목
        public final boolean preferredStock;  // 우선주
        public final boolean kospi200;        // KOSPI200

        public StockAttrs(boolean tradingHalt, boolean liquidation, boolean managed,
                          boolean preferredStock, boolean kospi200) {
            this.tradingHalt = tradingHalt;
            this.liquidation = liquidation;
            this.managed = managed;
            this.preferredStock = preferredStock;
            this.kospi200 = kospi200;
        }
    }
}
