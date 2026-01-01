package com.hyunha.batch.stock.call_kis_api_job.job;

import com.hyunha.batch.stock.call_kis_api_job.tasklet.FetchIndustryIndexPriceTasklet;
import com.hyunha.batch.stock.call_kis_api_job.tasklet.SaveIndustryIndexPriceTasklet;
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
public class IndustryCategoryPriceJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;

    @Bean
    public Job fetchIndexCategoryPriceJob(Step fetchIndexCategoryPriceStep,
                                          Step saveIndustryIndexPriceStep) {
        return new JobBuilder("fetchIndexCategoryPriceJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fetchIndexCategoryPriceStep)
                .next(saveIndustryIndexPriceStep)
                .build();
    }

    @Bean
    public Step fetchIndexCategoryPriceStep(FetchIndustryIndexPriceTasklet fetchIndustryIndexPriceTasklet) {
        return new StepBuilder("fetchIndexCategoryPriceStep", jobRepository)
                .tasklet(fetchIndustryIndexPriceTasklet, tx)
                .build();
    }

    @Bean
    public Step saveIndustryIndexPriceStep(SaveIndustryIndexPriceTasklet saveIndustryIndexPriceTasklet) {
        return new StepBuilder("saveIndustryIndexPriceStep", jobRepository)
                .tasklet(saveIndustryIndexPriceTasklet, tx)
                .build();
    }
}
