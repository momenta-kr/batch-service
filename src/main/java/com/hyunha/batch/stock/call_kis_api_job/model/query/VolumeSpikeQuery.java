package com.hyunha.batch.stock.call_kis_api_job.model.query;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VolumeSpikeQuery extends BaseRankingQuery {

    @Builder.Default
    private String mixn = "0"; // MIXN 필수

}
