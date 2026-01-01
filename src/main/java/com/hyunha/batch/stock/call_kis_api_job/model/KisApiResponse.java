package com.hyunha.batch.stock.call_kis_api_job.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KisApiResponse<T> {
    @JsonProperty("rt_cd")
    private String resultCode;
    @JsonProperty("msg_cd")
    private String messageCode;
    @JsonProperty("msg1")
    private String message;

    @JsonProperty("output1")
    private KisResponseMeta meta;

    @JsonProperty("output2")
    private List<T> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KisResponseMeta {
        @JsonProperty("zdiv")
        private String decimalPlaces;
        @JsonProperty("stat")
        private String marketStatus;
        @JsonProperty("crec")
        private String currentCount;
        @JsonProperty("trec")
        private String totalCount;
        @JsonProperty("nrec")
        private String recordCount;
    }
}
