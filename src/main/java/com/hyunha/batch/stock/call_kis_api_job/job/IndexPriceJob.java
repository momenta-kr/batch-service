package com.hyunha.batch.stock.call_kis_api_job.job;

import com.hyunha.batch.stock.call_kis_api_job.tasklet.FetchIndexPriceTasklet;
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
public class IndexPriceJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;

    @Bean
    public Job fetchIndexPriceJob(Step fetchIndexPriceStep,
                                  Step saveToRedisStep) {
        return new JobBuilder("fetchIndexPriceJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fetchIndexPriceStep)
                .next(saveToRedisStep)
                .build();
    }

    @Bean
    public Step fetchIndexPriceStep(FetchIndexPriceTasklet fetchIndexPriceTasklet) {
        return new StepBuilder("fetchIndexPriceStep", jobRepository)
                .tasklet(fetchIndexPriceTasklet, tx)
                .build();
    }
}
