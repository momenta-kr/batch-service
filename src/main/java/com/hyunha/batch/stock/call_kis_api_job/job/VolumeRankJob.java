package com.hyunha.batch.stock.call_kis_api_job.job;

import com.hyunha.batch.stock.call_kis_api_job.tasklet.FetchIndexPriceTasklet;
import com.hyunha.batch.stock.call_kis_api_job.tasklet.FetchVolumeRankTasklet;
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
public class VolumeRankJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;

    @Bean
    public Job fetchVolumeRankJob(Step fetchVolumeRankStep,
                                  Step saveToRedisStep) {
        return new JobBuilder("fetchVolumeRankJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fetchVolumeRankStep)
                .next(saveToRedisStep)
                .build();
    }

    @Bean
    public Step fetchVolumeRankStep(FetchVolumeRankTasklet fetchVolumeRankTasklet) {
        return new StepBuilder("fetchVolumeRankStep", jobRepository)
                .tasklet(fetchVolumeRankTasklet, tx)
                .build();
    }
}
