package com.hyunha.batch.stock.call_kis_api_job.tasklet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunha.batch.stock.call_kis_api_job.client.KisClient;
import com.hyunha.batch.stock.call_kis_api_job.enums.RedisKey;
import com.hyunha.batch.stock.call_kis_api_job.model.response.FluctuationResponse;
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
public class FetchTopGainersTasklet implements Tasklet {

    private final KisClient kisClient;
    private final ObjectMapper objectMapper;

    @Nullable
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        FluctuationResponse fluctuationResponse = kisClient.fetchTopGainers();
        ExecutionContext executionContext = chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();

        executionContext.put("key", RedisKey.TOP_GAINERS.name());
        executionContext.put(RedisKey.TOP_GAINERS.name(), objectMapper.writeValueAsString(fluctuationResponse));
        return RepeatStatus.FINISHED;
    }
}
