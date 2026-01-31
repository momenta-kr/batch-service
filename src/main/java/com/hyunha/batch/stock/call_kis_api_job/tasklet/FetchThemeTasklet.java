package com.hyunha.batch.stock.call_kis_api_job.tasklet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunha.batch.stock.call_kis_api_job.client.KisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@RequiredArgsConstructor
@Component
public class FetchThemeTasklet implements Tasklet {

    private final KisClient kisClient;
    private final ObjectMapper objectMapper;

    @Nullable
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {


        return null;
    }
}
