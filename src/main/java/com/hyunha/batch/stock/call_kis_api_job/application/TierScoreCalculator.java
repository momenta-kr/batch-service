package com.hyunha.batch.stock.call_kis_api_job.application;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class TierScoreCalculator {

    /**
     * 최종 결과: 점수 내림차순 리스트 = scoredSymbolsDesc
     */
    public record ScoredSymbol(String symbol, double score, Map<String, Integer> debugRanks) {}

    /**
     * "랭킹 소스" (API 하나 = 시그널 하나)
     * - name: 디버깅/로그/가중치 관리
     * - weight: 점수 합산 가중치
     * - fetchTopSymbols: top30(또는 topN) 심볼 리스트를 순서대로 반환
     */
    @Builder
    public static class RankingSource {
        private String name;
        private double weight;
        private int topN; // 지금은 30이지만 나중에 늘려도 됨
        private Function<String, CompletableFuture<List<String>>> fetchTopSymbols; // (token) -> top symbols

        public String name() { return name; }
        public double weight() { return weight; }
        public int topN() { return topN; }
        public CompletableFuture<List<String>> fetch(String token) { return fetchTopSymbols.apply(token); }
    }

    /**
     * 계산 옵션
     */
    @Getter
    @Builder
    public static class Options {
        /**
         * API 타임아웃(너가 client에서 orTimeout(4s) 이미 걸었지만, 여기서도 방어)
         */
        @Builder.Default
        private Duration timeout = Duration.ofSeconds(5);

        /**
         * 유니버스 크기(954 고정이지만, 코드 상 확장 가능)
         */
        @Builder.Default
        private int universeSize = 954;

        /**
         * rank -> score 변환 함수 (기본: 선형)
         * score = (N - rank + 1) / N
         */
        @Builder.Default
        private Function<RankContext, Double> rankToScore = ctx -> {
            int N = ctx.universeSize();
            int r = ctx.rank();
            if (r < 1) r = N;
            if (r > N) r = N;
            return (double) (N - r + 1) / (double) N;
        };
    }

    public record RankContext(int rank, int universeSize, String sourceName) {}

    /**
     * 핵심: scoredSymbolsDesc 만들기
     *
     * @param token KIS token
     * @param universeOrderedSymbols "954개 전체" fallback 순서(전일 시총순 추천)
     * @param sources RankingSource 목록 (API 추가해도 여기만 늘리면 됨)
     */
    public List<ScoredSymbol> computeScoredSymbolsDesc(
            String token,
            List<String> universeOrderedSymbols,
            List<RankingSource> sources,
            Options options
    ) {
        Objects.requireNonNull(token);
        Objects.requireNonNull(universeOrderedSymbols);
        Objects.requireNonNull(sources);
        Objects.requireNonNull(options);

        // 1) 유니버스 정규화 + 상위 954개로 제한
        List<String> universe = universeOrderedSymbols.stream()
                .map(TierScoreCalculator::normalizeSymbol)
                .distinct()
                .limit(options.getUniverseSize())
                .toList();

        if (universe.size() < options.getUniverseSize()) {
            throw new IllegalArgumentException("Universe size is smaller than " + options.getUniverseSize()
                    + ", got=" + universe.size());
        }

        // 2) 각 소스 호출 병렬 실행
        List<CompletableFuture<SourceResult>> futures = new ArrayList<>();
        for (RankingSource s : sources) {
            CompletableFuture<SourceResult> f = s.fetch(token)
                    .completeOnTimeout(List.of(), options.getTimeout().toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
                    .exceptionally(ex -> {
                        log.warn("[{}] fetch failed: {}", s.name(), ex.toString());
                        return List.of();
                    })
                    .thenApply(list -> new SourceResult(s, normalizeList(list)));
            futures.add(f);
        }

        List<SourceResult> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        // 3) 소스별 rank map 만들기 (topN만 유효)
        //    + 누락 종목은 30 + universeIndex+1 로 rank 채움
        Map<String, Double> totalScore = new HashMap<>(options.getUniverseSize());
        Map<String, Map<String, Integer>> debugRanks = new HashMap<>(options.getUniverseSize());
        for (String sym : universe) {
            totalScore.put(sym, 0.0);
            debugRanks.put(sym, new LinkedHashMap<>());
        }

        for (SourceResult sr : results) {
            RankingSource src = sr.source();
            List<String> topList = sr.topSymbols();

            // top 심볼 -> rank(1..topN)
            Map<String, Integer> topRank = new HashMap<>();
            int limit = Math.min(src.topN(), topList.size());
            for (int i = 0; i < limit; i++) {
                String sym = topList.get(i);
                // 유니버스 밖이면 무시(잡음 방지)
                if (!totalScore.containsKey(sym)) continue;
                // 중복 발생 시 첫 rank 유지
                topRank.putIfAbsent(sym, i + 1);
            }

            // 모든 유니버스 종목에 대해 rank를 확정(누락은 fallback)
            for (int idx = 0; idx < universe.size(); idx++) {
                String sym = universe.get(idx);

                int rank = topRank.getOrDefault(sym, src.topN() + (idx + 1)); // ✅ “안 걸린 종목은 순서대로”
                if (rank > options.getUniverseSize()) rank = options.getUniverseSize();

                double rankScore = options.getRankToScore().apply(new RankContext(rank, options.getUniverseSize(), src.name()));
                double add = rankScore * src.weight();

                totalScore.put(sym, totalScore.get(sym) + add);
                debugRanks.get(sym).put(src.name(), rank);
            }
        }

        // 4) 최종 내림차순 정렬
        // 동점이면 유니버스 순서(=fallback)로 안정화
        Map<String, Integer> universeOrder = new HashMap<>(universe.size());
        for (int i = 0; i < universe.size(); i++) universeOrder.put(universe.get(i), i);

        return universe.stream()
                .map(sym -> new ScoredSymbol(sym, totalScore.get(sym), debugRanks.get(sym)))
                .sorted((a, b) -> {
                    int c = Double.compare(b.score(), a.score());
                    if (c != 0) return c;
                    return Integer.compare(universeOrder.get(a.symbol()), universeOrder.get(b.symbol()));
                })
                .toList();
    }

    private record SourceResult(RankingSource source, List<String> topSymbols) {}

    private static List<String> normalizeList(List<String> symbols) {
        if (symbols == null) return List.of();
        return symbols.stream()
                .filter(Objects::nonNull)
                .map(TierScoreCalculator::normalizeSymbol)
                .filter(s -> !s.isBlank())
                .toList();
    }

    private static String normalizeSymbol(String raw) {
        // 국내: 6자리, 해외: 네 방식이면 그대로 쓰거나 별도 normalizer를 분리하는 게 좋음
        // 여기서는 "숫자만 남기고 6자리" 기준 (국내용)
        String digits = raw.replaceAll("\\D", "");
        if (digits.isEmpty()) return raw.trim();
        return String.format("%06d", Integer.parseInt(digits));
    }
}
