package com.hyunha.batch.stock.call_kis_api_job.application;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TierPersistenceService {

    private final JdbcTemplate jdbcTemplate;

    // -----------------------------
    // 입력 모델
    // -----------------------------
    public record ScoredSymbol(String symbol, double score) {}

    public enum Tier { T0, T1, T2, T3 }

    public record TierPolicy(Tier tier, int size) {}

    private static final List<TierPolicy> FIXED_POLICIES = List.of(
            new TierPolicy(Tier.T0, 120),
            new TierPolicy(Tier.T1, 180),
            new TierPolicy(Tier.T2, 350),
            new TierPolicy(Tier.T3, 304)
    );

    // -----------------------------
    // SQL (PostgreSQL Upsert)
    // -----------------------------
    private static final String UPSERT_DAILY_SQL = """
        INSERT INTO stock_tier_daily(as_of_date, symbol, tier, score, updated_at)
        VALUES (?, ?, ?, ?, now())
        ON CONFLICT (as_of_date, symbol) DO UPDATE
        SET tier = EXCLUDED.tier,
            score = EXCLUDED.score,
            updated_at = now()
        """;

    private static final String UPSERT_CURRENT_SQL = """
        INSERT INTO stock_tier_current(symbol, tier, score, as_of_date, updated_at)
        VALUES (?, ?, ?, ?, now())
        ON CONFLICT (symbol) DO UPDATE
        SET tier = EXCLUDED.tier,
            score = EXCLUDED.score,
            as_of_date = EXCLUDED.as_of_date,
            updated_at = now()
        """;

    // -----------------------------
    // 외부에서 호출하는 메서드
    // -----------------------------
    @Transactional
    public TierSaveResult saveDailyAndCurrent(LocalDate asOfDate, List<ScoredSymbol> scoredSymbolsDesc) {
        Objects.requireNonNull(asOfDate, "asOfDate");
        Objects.requireNonNull(scoredSymbolsDesc, "scoredSymbolsDesc");

        int totalNeed = FIXED_POLICIES.stream().mapToInt(TierPolicy::size).sum(); // 954
        if (scoredSymbolsDesc.size() < totalNeed) {
            throw new IllegalArgumentException("Need at least " + totalNeed + " symbols, got=" + scoredSymbolsDesc.size());
        }

        // 1) 심볼 정규화 + 상위 954개만 사용(혹시 더 많으면 잘라서 사용)
        List<ScoredSymbol> top = scoredSymbolsDesc.stream()
                .limit(totalNeed)
                .map(s -> new ScoredSymbol(normalizeSymbol(s.symbol()), s.score()))
                .collect(Collectors.toList());

        // 2) 티어 할당
        Map<Tier, List<ScoredSymbol>> tiered = allocate(top, FIXED_POLICIES);

        // 3) DB에 batch upsert
        int dailyRows = batchUpsertDaily(asOfDate, tiered);
        int currentRows = batchUpsertCurrent(asOfDate, tiered);

        // 4) 결과 리턴(검증용)
        Map<Tier, Integer> counts = new EnumMap<>(Tier.class);
        for (Tier t : Tier.values()) counts.put(t, tiered.getOrDefault(t, List.of()).size());

        return new TierSaveResult(asOfDate, counts, dailyRows, currentRows);
    }

    // -----------------------------
    // 결과 DTO
    // -----------------------------
    public record TierSaveResult(
            LocalDate asOfDate,
            Map<Tier, Integer> tierCounts,
            int dailyUpsertedRows,
            int currentUpsertedRows
    ) {}

    // -----------------------------
    // 내부 로직: 티어 나누기
    // -----------------------------
    private static Map<Tier, List<ScoredSymbol>> allocate(List<ScoredSymbol> ordered, List<TierPolicy> policies) {
        Map<Tier, List<ScoredSymbol>> map = new EnumMap<>(Tier.class);
        int idx = 0;
        for (TierPolicy p : policies) {
            List<ScoredSymbol> chunk = ordered.subList(idx, idx + p.size());
            map.put(p.tier(), List.copyOf(chunk));
            idx += p.size();
        }
        return map;
    }

    // -----------------------------
    // DB batch upsert
    // -----------------------------
    private int batchUpsertDaily(LocalDate asOfDate, Map<Tier, List<ScoredSymbol>> tiered) {
        List<Object[]> args = new ArrayList<>(954);
        for (var e : tiered.entrySet()) {
            Tier tier = e.getKey();
            for (ScoredSymbol s : e.getValue()) {
                args.add(new Object[]{
                        Date.valueOf(asOfDate),
                        s.symbol(),
                        tier.name(),
                        s.score()
                });
            }
        }
        int[] updated = jdbcTemplate.batchUpdate(UPSERT_DAILY_SQL, args);
        return Arrays.stream(updated).sum();
    }

    private int batchUpsertCurrent(LocalDate asOfDate, Map<Tier, List<ScoredSymbol>> tiered) {
        List<Object[]> args = new ArrayList<>(954);
        for (var e : tiered.entrySet()) {
            Tier tier = e.getKey();
            for (ScoredSymbol s : e.getValue()) {
                args.add(new Object[]{
                        s.symbol(),
                        tier.name(),
                        s.score(),
                        Date.valueOf(asOfDate)
                });
            }
        }
        int[] updated = jdbcTemplate.batchUpdate(UPSERT_CURRENT_SQL, args);
        return Arrays.stream(updated).sum();
    }

    // -----------------------------
    // 심볼 정규화(6자리)
    // -----------------------------
    private static String normalizeSymbol(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("\\D", "");
        if (digits.isEmpty()) return raw.trim();
        return String.format("%06d", Integer.parseInt(digits));
    }
}
