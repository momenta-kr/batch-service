package com.hyunha.batch.stock.call_kis_api_job.job;

import com.hyunha.batch.stock.call_kis_api_job.tasklet.FetchStockInfoTasklet;
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
public class StockInfoJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;

    private final FetchStockInfoTasklet fetchStockInfoTasklet;

    @Bean
    public Job fetchStockInfoJob(Step fetchStockInfoStep) {
        return new JobBuilder("fetchStockInfoJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fetchStockInfoStep)
                .build();
    }

    @Bean
    public Step fetchStockInfoStep() {
        return new StepBuilder("fetchStockInfoStep", jobRepository)
                .tasklet(fetchStockInfoTasklet, tx)
                .build();
    }
}
