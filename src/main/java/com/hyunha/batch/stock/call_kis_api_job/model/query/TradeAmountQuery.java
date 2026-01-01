package com.hyunha.batch.stock.call_kis_api_job.model.query;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TradeAmountQuery extends BaseRankingQuery {

    @Builder.Default
    private String nday = "0"; // NDAY 필수
    @Builder.Default
    private String prc1 = "0"; // PRC1 필수
    @Builder.Default
    private String prc2 = "999999999999"; // PRC2 필수
}
