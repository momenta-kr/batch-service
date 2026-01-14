package com.hyunha.batch.stock.call_kis_api_job.infra.kis;

import com.hyunha.batch.stock.call_kis_api_job.application.RankingClient;
import com.hyunha.batch.stock.call_kis_api_job.client.TokenProvider;
import com.hyunha.batch.stock.call_kis_api_job.model.response.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Component
public class KisRankingClient2 implements RankingClient {

    @Qualifier(value = "kisRestClient")
    private final RestClient kisRestClient;
    private final TokenProvider tokenProvider;
    private final KisProperties kisProperties;

    private @NonNull Consumer<HttpHeaders> getCommonHttpHeaders(String transactionId) {
        return httpHeaders -> {
            httpHeaders.setBearerAuth(tokenProvider.getAccessToken());
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("appkey", kisProperties.getAppKey());
            httpHeaders.set("appsecret", kisProperties.getAppSecret());
            httpHeaders.set("tr_id", transactionId);
            httpHeaders.set("custtype", "P");
        };
    }

    /**
     * 시총순위
     */
    @Override
    public MarketCapRankResponse fetchMarketCapRanking() {
        return kisRestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/uapi/domestic-stock/v1/ranking/market-cap")
                                .queryParam("fid_input_price_2", "")
                                .queryParam("fid_cond_mrkt_div_code", "J")
                                .queryParam("fid_cond_scr_div_code", "20174")
                                .queryParam("fid_div_cls_code", "0")
                                .queryParam("fid_input_iscd", "0001")
                                .queryParam("fid_trgt_cls_code", "0")
                                .queryParam("fid_trgt_exls_cls_code", "0")
                                .queryParam("fid_input_price_1", "")
                                .queryParam("fid_vol_cnt", "")
                                .build())
                .headers(getCommonHttpHeaders("FHPST01740000"))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> {
                            String body = new String(res.getBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                            throw new IllegalStateException("KIS API failed: status=" + res.getStatusCode() + " body=" + body);
                        })
                .body(MarketCapRankResponse.class);
    }

    @Override
    public VolumeRankResponse fetchVolumeRanking() {
        return kisRestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                                .queryParam("FID_COND_SCR_DIV_CODE", "20171")
                                .queryParam("FID_INPUT_ISCD", "0000")
                                .queryParam("FID_DIV_CLS_CODE", "0")
                                .queryParam("FID_BLNG_CLS_CODE", "3")
                                .queryParam("FID_TRGT_CLS_CODE", "111111111")
                                .queryParam("FID_TRGT_EXLS_CLS_CODE", "0000000000")
                                .queryParam("FID_INPUT_PRICE_1", "")
                                .queryParam("FID_INPUT_PRICE_2", "")
                                .queryParam("FID_VOL_CNT", "")
                                .queryParam("FID_INPUT_DATE_1", "")

                                .build())
                .headers(getCommonHttpHeaders("FHPST01710000"))
                .retrieve()
                .body(VolumeRankResponse.class);
    }


    /**
     * 국내주식 등락률 순위
     */
    public FluctuationResponse fetchTopGainers() {
        return fetchFluctuation(true);
    }

    /**
     * 국내주식 등락률 순위
     */
    public FluctuationResponse fetchTopLosers() {
        return fetchFluctuation(false);
    }


    private FluctuationResponse fetchFluctuation(boolean isUp) {
        return kisRestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/uapi/domestic-stock/v1/ranking/fluctuation")
                                .queryParam("fid_rsfl_rate2", "")
                                .queryParam("fid_cond_mrkt_div_code", "J")
                                .queryParam("fid_cond_scr_div_code", "20170")
                                .queryParam("fid_input_iscd", "0001")
                                .queryParam("fid_rank_sort_cls_code", isUp ? "0" : "1")
                                .queryParam("fid_input_cnt_1", "0")
                                .queryParam("fid_prc_cls_code", isUp ? "0" : "1")
                                .queryParam("fid_input_price_1", "")
                                .queryParam("fid_input_price_2", "")
                                .queryParam("fid_vol_cnt", "")
                                .queryParam("fid_trgt_cls_code", "0")
                                .queryParam("fid_trgt_exls_cls_code", "0")
                                .queryParam("fid_div_cls_code", "0")
                                .queryParam("fid_rsfl_rate1", "")
                                .build())
                .headers(getCommonHttpHeaders("FHPST01700000"))
                .retrieve()
                .body(FluctuationResponse.class);
    }

    @Override
    public StocksOfInterestRankResponse fetchStocksOfInterestRanking() {
        return kisRestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/uapi/domestic-stock/v1/ranking/top-interest-stock")
                                .queryParam("fid_input_iscd_2", "000000")
                                .queryParam("fid_cond_mrkt_div_code", "J")
                                .queryParam("fid_cond_scr_div_code", "20180")
                                .queryParam("fid_input_iscd", "0001")
                                .queryParam("fid_trgt_cls_code", "0")
                                .queryParam("fid_trgt_exls_cls_code", "0")
                                .queryParam("fid_input_price_1", "")
                                .queryParam("fid_input_price_2", "")
                                .queryParam("fid_vol_cnt", "")
                                .queryParam("fid_div_cls_code", "0")
                                .queryParam("fid_input_cnt_1", "1")
                                .build())
                .headers(getCommonHttpHeaders("FHPST01800000"))
                .retrieve()
                .body(StocksOfInterestRankResponse.class);
    }

    @Override
    public TradeStrengthResponse fetchTradeStrengthRanking() {
        return kisRestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/uapi/domestic-stock/v1/ranking/volume-power")
                                .queryParam("fid_trgt_exls_cls_code", "0")
                                .queryParam("fid_cond_mrkt_div_code", "J")
                                .queryParam("fid_cond_scr_div_code", "20168")
                                .queryParam("fid_input_iscd", "0000")
                                .queryParam("fid_div_cls_code", "0")
                                .queryParam("fid_input_price_1", "")
                                .queryParam("fid_input_price_2", "")
                                .queryParam("fid_vol_cnt", "")
                                .queryParam("fid_trgt_cls_code", "0")
                                .build())
                .headers(getCommonHttpHeaders("FHPST01680000"))
                .retrieve()
                .body(TradeStrengthResponse.class);
    }
}
