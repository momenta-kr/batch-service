package com.hyunha.batch.stock.call_kis_api_job.tasklet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunha.batch.stock.call_kis_api_job.client.KisClient;
import com.hyunha.batch.stock.call_kis_api_job.model.response.IndustryIndexPriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FetchIndustryIndexPriceTasklet implements Tasklet {

    private final KisClient kisClient;
    private final ObjectMapper objectMapper;

    @Nullable
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        IndustryIndexPriceResponse industryIndexPriceResponse = kisClient.fetchIndustryIndexPrice();
        chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .put("industryIndexPrice", objectMapper.writeValueAsString(industryIndexPriceResponse));
        return RepeatStatus.FINISHED;
    }
}
