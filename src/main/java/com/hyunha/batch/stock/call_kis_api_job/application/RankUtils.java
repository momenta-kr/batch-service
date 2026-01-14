package com.hyunha.batch.stock.call_kis_api_job.application;

import java.util.*;
import java.util.function.Function;

public final class RankUtils {

    private RankUtils() {}

    /**
     * topList에 있는 종목은 1..topN rank,
     * topList에 없는 종목은 universeOrdered 순서대로 topN+1..N rank 부여
     *
     * @param topList            API에서 받은 top 결과(예: 30개)
     * @param symbolExtractor    T -> symbol
     * @param universeOrdered    954개 전체 유니버스(전일 시총 desc 등) - fallback 기준
     */
    public static <T> Map<String, Integer> buildFullRankMap(
            List<T> topList,
            Function<T, String> symbolExtractor,
            List<String> universeOrdered
    ) {
        int n = universeOrdered.size();
        Map<String, Integer> rank = new HashMap<>(n * 2);

        // 1) top에 있는 애들 rank 1..k
        int r = 1;
        for (T it : topList) {
            String s = normalizeSymbol(symbolExtractor.apply(it));
            if (s == null || s.isBlank()) continue;
            if (!rank.containsKey(s)) {
                rank.put(s, r++);
            }
        }

        // 2) 나머지(미포함)는 유니버스 순서대로 rank 이어붙이기
        for (String s0 : universeOrdered) {
            String s = normalizeSymbol(s0);
            if (s == null || s.isBlank()) continue;
            if (!rank.containsKey(s)) {
                rank.put(s, r++);
                if (r > n + 1) break;
            }
        }

        return rank;
    }

    /** "005930" 형태로 정규화 (필요시 커스텀) */
    public static String normalizeSymbol(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        // 숫자 6자리만 쓰고 싶으면:
        s = s.replaceAll("\\D", "");
        if (s.isEmpty()) return null;
        return String.format("%06d", Integer.parseInt(s));
    }
}
