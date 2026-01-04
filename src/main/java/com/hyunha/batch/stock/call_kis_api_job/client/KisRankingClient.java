package com.hyunha.batch.stock.call_kis_api_job.client;

import com.hyunha.batch.stock.call_kis_api_job.enums.KisOverseasRankingApi;
import com.hyunha.batch.stock.call_kis_api_job.model.KisApiResponse;
import com.hyunha.batch.stock.call_kis_api_job.model.OverseasStockRankingItem;
import com.hyunha.batch.stock.call_kis_api_job.model.query.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.*;

@Slf4j
@Service
public class KisRankingClient {

    private final RestTemplate kisRestTemplate;
    private final ExecutorService kisExecutor;

    @Value("${kis.base-url}") private String baseUrl;
    @Value("${kis.app-key}")  private String appKey;
    @Value("${kis.app-secret}") private String appSecret;


    public KisRankingClient(RestTemplate kisRestTemplate, ExecutorService kisExecutor) {
        this.kisRestTemplate = kisRestTemplate;
        this.kisExecutor = kisExecutor;
    }

    public CompletableFuture<KisApiResponse<OverseasStockRankingItem>> tradeAmount(String token, TradeAmountQuery q) {
        return supply(() -> get(token, KisOverseasRankingApi.TRADE_AMOUNT, q));
    }

    public CompletableFuture<KisApiResponse<OverseasStockRankingItem>> volumeSpike(String token, VolumeSpikeQuery q) {
        return supply(() -> get(token, KisOverseasRankingApi.VOLUME_SPIKE, q));
    }

    public CompletableFuture<KisApiResponse<OverseasStockRankingItem>> buyStrength(String token, BuyStrengthQuery q) {
        return supply(() -> get(token, KisOverseasRankingApi.BUY_STRENGTH, q));
    }

    // 가격급등락: 급등/급락 두 번
    public CompletableFuture<KisApiResponse<OverseasStockRankingItem>> priceMoveUp(String token, PriceMoveQuery base) {
        PriceMoveQuery q = PriceMoveQuery.builder()
                .keyb(base.getKeyb()).auth(base.getAuth())
                .excd(base.getExcd()).volRang(base.getVolRang())
                .mixn(base.getMixn())
                .gubn("1") // 급등
                .build();
        return supply(() -> get(token, KisOverseasRankingApi.PRICE_CHANGE, q));
    }

    public CompletableFuture<KisApiResponse<OverseasStockRankingItem>> priceMoveDown(String token, PriceMoveQuery base) {
        PriceMoveQuery q = PriceMoveQuery.builder()
                .keyb(base.getKeyb()).auth(base.getAuth())
                .excd(base.getExcd()).volRang(base.getVolRang())
                .mixn(base.getMixn())
                .gubn("0") // 급락
                .build();
        return supply(() -> get(token, KisOverseasRankingApi.PRICE_CHANGE, q));
    }

    private KisApiResponse<OverseasStockRankingItem> get(String token, KisOverseasRankingApi api, BaseRankingQuery q) {
        q.validate();

        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(baseUrl + api.getPath())
                // 공통
                .queryParam("KEYB", nullToEmpty(q.getKeyb()))
                .queryParam("AUTH", nullToEmpty(q.getAuth()))
                .queryParam("EXCD", q.getExcd())
                .queryParam("VOL_RANG", nullToEmpty(q.getVolRang()));

        // 자식별 추가 파라미터
        if (q instanceof TradeAmountQuery ta) {
            b.queryParam("NDAY", ta.getNday())
                    .queryParam("PRC1", ta.getPrc1())
                    .queryParam("PRC2", ta.getPrc2());
        } else if (q instanceof VolumeSpikeQuery vs) {
            b.queryParam("MIXN", vs.getMixn());
        } else if (q instanceof BuyStrengthQuery bs) {
            b.queryParam("NDAY", bs.getNday());
        } else if (q instanceof PriceMoveQuery pm) {
            b.queryParam("GUBN", pm.getGubn())
                    .queryParam("MIXN", pm.getMixn());
        }

        String url = b.build(true).toUriString();

        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));
        h.setContentType(MediaType.APPLICATION_JSON);
        h.add("appkey", appKey);
        h.add("appsecret", appSecret);
        h.add("tr_id", api.getTrId());
        h.add("custtype", "P"); // 개인

        ResponseEntity<KisApiResponse<OverseasStockRankingItem>> res =
                kisRestTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(null, h),
                        new ParameterizedTypeReference<>() {}
                );

        return res.getBody();
    }

    private <T> CompletableFuture<T> supply(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
                    try { return task.call(); }
                    catch (Exception e) { throw new CompletionException(e); }
                }, kisExecutor)
                .orTimeout(4, TimeUnit.SECONDS)
                .exceptionally(ex -> null);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
