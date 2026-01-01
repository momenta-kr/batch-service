package com.hyunha.batch.stock.call_kis_api_job.model.query;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PriceMoveQuery extends BaseRankingQuery {

    @Builder.Default
    private String gubn = "1"; // 0(급락), 1(급등)
    @Builder.Default
    private String mixn = "0"; // 필수

}
