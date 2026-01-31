package com.hyunha.batch.stock.call_kis_api_job.job;

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
public class ThemeJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;

    @Bean
    public Job fetchThemeJob() {
        return new JobBuilder("fetchThemeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start()
    }

    @Bean
    public Step fetchThemeStep() {
        return new StepBuilder("fetchThemeStep", jobRepository)
                .tasklet(, tx)
                .build();
    }
}
