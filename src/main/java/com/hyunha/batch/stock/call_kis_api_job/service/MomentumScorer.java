package com.hyunha.batch.stock.call_kis_api_job.service;

import com.hyunha.batch.stock.call_kis_api_job.model.SymbolFeature;
import org.springframework.stereotype.Component;

@Component
public class MomentumScorer {

    // 간단한 min-max normalize (스냅샷 내부에서만)
    public double normalize(double v, double min, double max) {
        if (max - min < 1e-9) return 0.0;
        double x = (v - min) / (max - min);
        return Math.max(0, Math.min(1, x));
    }

    public double score(SymbolFeature f) {
        // 이미 0~1로 들어왔다고 가정하면 더 단순 가능
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
}
