package com.hyunha.batch.stock.call_kis_api_job.job;

import com.hyunha.batch.stock.call_kis_api_job.tasklet.FetchKisRankingTasklet;
import com.hyunha.batch.stock.call_kis_api_job.tasklet.SaveTop20ToRedisTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Component
public class CallKisApi {

    private final FetchKisRankingTasklet fetchKisRankingTasklet;
    private final SaveTop20ToRedisTasklet saveTop20ToRedisTasklet;

    @Bean
    public Job callKisApiJob(JobRepository jobRepository,
                             Step fetchAndComputeMomentumStep,
                             Step saveTop20ToRedisStep) {
        return new JobBuilder("callKisApiJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fetchAndComputeMomentumStep)
                .next(saveTop20ToRedisStep)          // ✅ Step2 추가
                .build();
    }

    @Bean
    public Step fetchAndComputeMomentumStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("fetchAndComputeMomentumStep", jobRepository)
                .tasklet(fetchKisRankingTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step saveTop20ToRedisStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("saveTop20ToRedisStep", jobRepository)
                .tasklet(saveTop20ToRedisTasklet, transactionManager)
                .build();
    }
}
