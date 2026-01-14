package com.hyunha.batch.stock.call_kis_api_job.job;

import com.hyunha.batch.stock.call_kis_api_job.tasklet.FetchVariousRankingForTierTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Component
public class VariousRankingForTierJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;


    @Bean
    public Job fetchVariousRankingForTierJob(Step fetchVariousRankingForTierStep) {
        return new JobBuilder("fetchVariousRankingForTierJob", jobRepository)
                .start(fetchVariousRankingForTierStep)
                .build();
    }

    @Bean
    public Step fetchVariousRankingForTierStep(FetchVariousRankingForTierTasklet fetchVariousRankingForTierTasklet) {
        return new StepBuilder("fetchVariousRankingForTierStep", jobRepository)
                .tasklet(fetchVariousRankingForTierTasklet, tx)
                .build();
    }
}
