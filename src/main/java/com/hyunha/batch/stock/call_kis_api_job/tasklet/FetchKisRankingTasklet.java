package com.hyunha.batch.stock.call_kis_api_job.tasklet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunha.batch.stock.call_kis_api_job.client.KisRankingClient;
import com.hyunha.batch.stock.call_kis_api_job.client.TokenProvider;
import com.hyunha.batch.stock.call_kis_api_job.model.KisApiResponse;
import com.hyunha.batch.stock.call_kis_api_job.model.OverseasStockRankingItem;
import com.hyunha.batch.stock.call_kis_api_job.model.SymbolFeature;
import com.hyunha.batch.stock.call_kis_api_job.model.query.BuyStrengthQuery;
import com.hyunha.batch.stock.call_kis_api_job.model.query.PriceMoveQuery;
import com.hyunha.batch.stock.call_kis_api_job.model.query.TradeAmountQuery;
import com.hyunha.batch.stock.call_kis_api_job.model.query.VolumeSpikeQuery;
import com.hyunha.batch.stock.call_kis_api_job.service.MomentumFeatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class FetchKisRankingTasklet implements Tasklet {

    private final KisRankingClient kisRankingClient;
    private final TokenProvider tokenProvider;
    private final MomentumFeatureService featureService;
    private final ObjectMapper objectMapper;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws JsonProcessingException {
        String token = tokenProvider.getAccessToken();

        // ✅ 여기서 거래소/조건을 “하나의 기준”으로 잡아야 함
        // 배치 파라미터로 받을 수도 있는데, 일단 NAS + 전체 조건 기본값으로 둠
        String excd = "NAS";
        String volRang = "0";

        TradeAmountQuery tradeQ = TradeAmountQuery.builder()
                .excd(excd)
                .volRang(volRang)
                .nday("0")
                .prc1("0")
                .prc2("999999999999")
                .build();

        VolumeSpikeQuery volQ = VolumeSpikeQuery.builder()
                .excd(excd)
                .volRang(volRang)
                .mixn("3") // 예: 5분전
                .build();

        BuyStrengthQuery buyQ = BuyStrengthQuery.builder()
                .excd(excd)
                .volRang(volRang)
                .nday("0")
                .build();

        PriceMoveQuery priceBaseQ = PriceMoveQuery.builder()
                .excd(excd)
                .volRang(volRang)
                .mixn("3") // 예: 5분전
                .build();

        // ✅ 비동기 호출
        CompletableFuture<KisApiResponse<OverseasStockRankingItem>> fTrade =
                kisRankingClient.tradeAmount(token, tradeQ);

        CompletableFuture<KisApiResponse<OverseasStockRankingItem>> fVol =
                kisRankingClient.volumeSpike(token, volQ);

        CompletableFuture<KisApiResponse<OverseasStockRankingItem>> fBuy =
                kisRankingClient.buyStrength(token, buyQ);

        // ✅ 가격급등락: 급등/급락 두 번
        CompletableFuture<KisApiResponse<OverseasStockRankingItem>> fUp =
                kisRankingClient.priceMoveUp(token, priceBaseQ);

        CompletableFuture<KisApiResponse<OverseasStockRankingItem>> fDown =
                kisRankingClient.priceMoveDown(token, priceBaseQ);

        CompletableFuture.allOf(fTrade, fVol, fBuy, fUp, fDown).join();

        List<OverseasStockRankingItem> tradeAmount = items(fTrade.getNow(null));
        List<OverseasStockRankingItem> volumeSpike = items(fVol.getNow(null));
        List<OverseasStockRankingItem> buyStrength = items(fBuy.getNow(null));

        // ✅ 급등 + 급락 합쳐서 priceMove 리스트로 (기존 merge 시그니처 유지)
        List<OverseasStockRankingItem> priceMove = new ArrayList<>();
        priceMove.addAll(items(fUp.getNow(null)));
        priceMove.addAll(items(fDown.getNow(null)));

        var merged = featureService.merge(tradeAmount, volumeSpike, priceMove, buyStrength);
        List<SymbolFeature> top20 = featureService.topN(merged, 20);

        log.info("Top20 momentum snapshot size={}", top20.size());
        top20.forEach(f ->
                log.debug("TOP sym={} name={} score={} trade={} vol={} move={} buy={}",
                        f.getSymbol(),
                        f.getName(),
                        round3(f.getMomentumScore()),
                        round3(f.getTradeAmountScore()),
                        round3(f.getVolumeSpikeScore()),
                        round3(f.getPriceMoveScore()),
                        round3(f.getBuyStrengthScore())
                )
        );

        chunkContext.getStepContext().getStepExecution().getJobExecution()
                .getExecutionContext().put("top20", objectMapper.writeValueAsString(top20));

        return RepeatStatus.FINISHED;
    }

    private List<OverseasStockRankingItem> items(KisApiResponse<OverseasStockRankingItem> res) {
        if (res == null || res.getItems() == null) return List.of();
        return res.getItems();
    }

    private String round3(Double v) {
        if (v == null) return "null";
        return String.format("%.3f", v);
    }
}
