package com.hyunha.batch.stock.call_kis_api_job.tasklet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunha.batch.stock.call_kis_api_job.client.KisClient;
import com.hyunha.batch.stock.call_kis_api_job.enums.RedisKey;
import com.hyunha.batch.stock.call_kis_api_job.model.response.IndexPriceResponse;
import com.hyunha.batch.stock.call_kis_api_job.model.response.VolumeRankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FetchVolumeRankTasklet implements Tasklet {

    private final KisClient kisClient;
    private final ObjectMapper objectMapper;

    @Nullable
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        VolumeRankResponse volumeRankResponse = kisClient.fetchVolumeRanking();
        ExecutionContext executionContext = chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();

        executionContext.put("key", RedisKey.VOLUME_RANK.name());
        executionContext.put(RedisKey.VOLUME_RANK.name(), objectMapper.writeValueAsString(volumeRankResponse));
        return RepeatStatus.FINISHED;
    }
}
