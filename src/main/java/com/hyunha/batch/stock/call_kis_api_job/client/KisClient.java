package com.hyunha.batch.stock.call_kis_api_job.client;

import com.hyunha.batch.stock.call_kis_api_job.infra.kis.KisProperties;
import com.hyunha.batch.stock.call_kis_api_job.model.response.FluctuationResponse;
import com.hyunha.batch.stock.call_kis_api_job.model.response.IndexPriceResponse;
import com.hyunha.batch.stock.call_kis_api_job.model.response.IndustryIndexPriceResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Component
public class KisClient {

    private final RestClient kisRestClient;
    private final TokenProvider tokenProvider;
    private final KisProperties kisProperties;

    public IndustryIndexPriceResponse fetchIndustryIndexPrice() {
        return kisRestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/uapi/domestic-stock/v1/quotations/inquire-index-category-price")
                                .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                                .queryParam("FID_INPUT_ISCD", "0001")
                                .queryParam("FID_COND_SCR_DIV_CODE", "20214")
                                .queryParam("FID_MRKT_CLS_CODE", "K")
                                .queryParam("FID_BLNG_CLS_CODE", "3")

                                .build())
                .headers(getCommonHttpHeaders("FHPUP02140000"))
                .retrieve()
                .body(IndustryIndexPriceResponse.class);
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

    public IndexPriceResponse fetchIndexPrice() {
        return kisRestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/uapi/domestic-stock/v1/ranking/fluctuation")
                                .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                                .queryParam("FID_INPUT_ISCD", "0001")
                                .build())
                .headers(getCommonHttpHeaders("FHPUP02100000"))
                .retrieve()
                .body(IndexPriceResponse.class);
    }

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
}
