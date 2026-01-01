package com.hyunha.batch.stock.call_kis_api_job.job;

import com.hyunha.batch.stock.call_kis_api_job.tasklet.FetchTopGainersTasklet;
import com.hyunha.batch.stock.call_kis_api_job.tasklet.FetchTopLosersTasklet;
import com.hyunha.batch.stock.call_kis_api_job.tasklet.SaveToRedisTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Component
public class FluctuationJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;
    private final FetchTopGainersTasklet fetchTopGainersTasklet;
    private final FetchTopLosersTasklet fetchTopLosersTasklet;

    @Bean
    public Job fetchFluctuationJob(Step fetchTopGainersStep,
                                   Step fetchTopLosernsStep,
                                   Step saveToRedisStep) {
        return new JobBuilder("fetchFluctuationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fetchTopGainersStep)
                .next(saveToRedisStep)
                .next(fetchTopLosernsStep)
                .next(saveToRedisStep)
                .build();
    }

    @Bean
    public Step fetchTopGainersStep() {
        return new StepBuilder("fetchTopGainersStep", jobRepository)
                .tasklet(fetchTopGainersTasklet, tx)
                .build();
    }

    @Bean
    public Step fetchTopLosernsStep() {
        return new StepBuilder("fetchTopLosernsStep", jobRepository)
                .tasklet(fetchTopLosersTasklet, tx)
                .build();
    }

    @Bean
    public Step saveToRedisStep(SaveToRedisTasklet saveToRedisTasklet) {
        return new StepBuilder("saveToRedisStep", jobRepository)
                .tasklet(saveToRedisTasklet, tx)
                .build();
    }
}
