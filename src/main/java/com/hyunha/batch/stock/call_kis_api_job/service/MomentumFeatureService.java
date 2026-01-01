package com.hyunha.batch.stock.call_kis_api_job.service;

import com.hyunha.batch.stock.call_kis_api_job.model.OverseasStockRankingItem;
import com.hyunha.batch.stock.call_kis_api_job.model.SymbolFeature;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MomentumFeatureService {

    // 랭킹 점수화: 1등=1.0, 50등=0.02
    private double rankScore(String rankStr, int maxRank) {
        if (rankStr == null || rankStr.isBlank()) return 0.0;
        int rank = Integer.parseInt(rankStr.trim());
        return Math.max(0.0, (maxRank - rank + 1) / (double) maxRank);
    }

    private BigDecimal bd(String s) {
        if (s == null || s.isBlank()) return null;
        return new BigDecimal(s.trim());
    }

    private String displayName(OverseasStockRankingItem i) {
        // name/knam 둘 중 있는 걸 쓰기
        if (i.getKoreanName() != null && !i.getKoreanName().isBlank()) return i.getKoreanName();
        if (i.getKoreanNameAlt() != null && !i.getKoreanNameAlt().isBlank()) return i.getKoreanNameAlt();
        if (i.getEnglishName() != null && !i.getEnglishName().isBlank()) return i.getEnglishName();
        return i.getEnglishNameAlt();
    }

    private void applyCommon(SymbolFeature f, OverseasStockRankingItem i) {
        if (f.getRealtimeSymbol() == null) f.setRealtimeSymbol(i.getRealtimeSymbol());
        if (f.getExchangeCode() == null)  f.setExchangeCode(i.getExchangeCode());
        if (f.getName() == null)          f.setName(displayName(i));
        if (f.getLastPrice() == null)     f.setLastPrice(bd(i.getLastPrice()));
    }

    public Map<String, SymbolFeature> merge(
            List<OverseasStockRankingItem> tradeAmountItems,
            List<OverseasStockRankingItem> volumeSpikeItems,
            List<OverseasStockRankingItem> priceMoveItems,
            List<OverseasStockRankingItem> buyStrengthItems
    ) {
        Map<String, SymbolFeature> map = new HashMap<>();

        int maxRank = 20; // 보통 랭킹 Top20 가정 (필요시 meta.nrec로 바꿔도 됨)

        // 거래대금
        for (OverseasStockRankingItem i : tradeAmountItems) {
            map.computeIfAbsent(i.getSymbol(), k -> SymbolFeature.builder().symbol(k).build());
            SymbolFeature f = map.get(i.getSymbol());
            applyCommon(f, i);
            f.setTradeAmountScore(rankScore(i.getRank(), maxRank));
        }

        // 거래량 급증
        for (OverseasStockRankingItem i : volumeSpikeItems) {
            map.computeIfAbsent(i.getSymbol(), k -> SymbolFeature.builder().symbol(k).build());
            SymbolFeature f = map.get(i.getSymbol());
            applyCommon(f, i);
            f.setVolumeSpikeScore(rankScore(i.getRank(), maxRank));
        }

        // 가격 급등락
        for (OverseasStockRankingItem i : priceMoveItems) {
            map.computeIfAbsent(i.getSymbol(), k -> SymbolFeature.builder().symbol(k).build());
            SymbolFeature f = map.get(i.getSymbol());
            applyCommon(f, i);
            f.setPriceMoveScore(rankScore(i.getRank(), maxRank));
        }

        // 체결강도
        for (OverseasStockRankingItem i : buyStrengthItems) {
            map.computeIfAbsent(i.getSymbol(), k -> SymbolFeature.builder().symbol(k).build());
            SymbolFeature f = map.get(i.getSymbol());
            applyCommon(f, i);
            f.setBuyStrengthScore(rankScore(i.getRank(), maxRank));
        }

        return map;
    }

    public double momentumScore(SymbolFeature f) {
        // 가중치: 거래대금(돈) + 거래량급증(초기) + 급등락(변동성) + 체결강도(진짜)
        double wTrade = 0.35;
        double wVol   = 0.30;
        double wMove  = 0.20;
        double wBuy   = 0.15;

        return wTrade * nz(f.getTradeAmountScore())
             + wVol   * nz(f.getVolumeSpikeScore())
             + wMove  * nz(f.getPriceMoveScore())
             + wBuy   * nz(f.getBuyStrengthScore());
    }

    private double nz(Double v) { return v == null ? 0.0 : v; }

    public List<SymbolFeature> topN(Map<String, SymbolFeature> map, int n) {
        return map.values().stream()
                .peek(f -> f.setMomentumScore(momentumScore(f)))
                .sorted(Comparator.comparing(SymbolFeature::getMomentumScore).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
}
