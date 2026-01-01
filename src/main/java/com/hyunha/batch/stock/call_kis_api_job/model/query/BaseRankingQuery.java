package com.hyunha.batch.stock.call_kis_api_job.model.query;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseRankingQuery {
    @Builder.Default
    private String keyb = "";     // KEYB (공백)
    @Builder.Default
    private String auth = "";     // AUTH (공백)

    private String excd;          // EXCD (필수)
    @Builder.Default
    private String volRang = "0"; // VOL_RANG (0=전체)

    public void validate() {
        if (excd == null || excd.isBlank()) {
            throw new IllegalArgumentException("EXCD(excd) is required");
        }
    }
}
