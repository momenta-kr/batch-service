package com.hyunha.batch.stock.call_kis_api_job.job;

import com.hyunha.batch.stock.call_kis_api_job.tasklet.FetchNaverNewsTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Component
public class NaverNewsJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;

    @Bean
    public Job fetchNaverNewsJob(Step fetchNaverNewsStep) {
        return new JobBuilder("fetchNaverNewsJob", jobRepository)
                .start(fetchNaverNewsStep)
                .build();
    }

    @Bean
    public Step fetchNaverNewsStep(FetchNaverNewsTasklet fetchNaverNewsTasklet) {
        return new StepBuilder("fetchNaverNewsStep", jobRepository)
                .tasklet(fetchNaverNewsTasklet, tx)
                .build();
    }
}
