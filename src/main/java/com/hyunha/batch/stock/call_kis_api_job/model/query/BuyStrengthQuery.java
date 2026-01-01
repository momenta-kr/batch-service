package com.hyunha.batch.stock.call_kis_api_job.model.query;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BuyStrengthQuery extends BaseRankingQuery {

    @Builder.Default
    private String nday = "0"; // 문서상 필수

}
