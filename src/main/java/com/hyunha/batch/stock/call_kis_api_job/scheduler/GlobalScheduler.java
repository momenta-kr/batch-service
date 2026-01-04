package com.hyunha.batch.stock.call_kis_api_job.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class GlobalScheduler {

    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;

    private final Job fetchIndexCategoryPriceJob;
    private final Job fetchFluctuationJob;
    private final Job fetchIndexPriceJob;
    private final Job fetchVariousRankingForTierJob;
    private final Job fetchNaverNewsJob;

    @Scheduled(fixedDelay = 86_400_000)
    public void runFetchNaverNewsJob() {
        if (isJobRunning("fetchNaverNewsJob")) {
            log.info("Job already running, skip");
            return;
        }

        JobParameters params = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis()) // ⭐ 중요
                .toJobParameters();

        try {
            jobLauncher.run(fetchNaverNewsJob, params);
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.warn("Batch job skipped: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 100_000)
    public void runFetchVariousRankingForTierJob() {
        if (isJobRunning("fetchVariousRankingForTierJob")) {
            log.info("Job already running, skip");
            return;
        }

        JobParameters params = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis()) // ⭐ 중요
                .toJobParameters();

        try {
            jobLauncher.run(fetchVariousRankingForTierJob, params);
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.warn("Batch job skipped: {}", e.getMessage());
        }
    }


    @Scheduled(fixedDelay = 100_000)
    public void runFetchIndustryIndexPriceJob() {
        if (isJobRunning("fetchIndexCategoryPriceJob")) {
            log.info("Job already running, skip");
            return;
        }

        JobParameters params = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis()) // ⭐ 중요
                .toJobParameters();

        try {
            jobLauncher.run(fetchIndexCategoryPriceJob, params);
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.warn("Batch job skipped: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 100_000)
    public void runFetchFluctuationJob() {
        if (isJobRunning("fetchFluctuationJob")) {
            log.info("Job already running, skip");
            return;
        }

        JobParameters params = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis()) // ⭐ 중요
                .toJobParameters();

        try {
            jobLauncher.run(fetchFluctuationJob, params);
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.warn("Batch job skipped: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 100_000)
    public void runFetchIndexPriceJob() {
        if (isJobRunning("fetchIndexPriceJob")) {
            log.info("Job already running, skip");
            return;
        }

        JobParameters params = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis()) // ⭐ 중요
                .toJobParameters();

        try {
            jobLauncher.run(fetchIndexPriceJob, params);
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.warn("Batch job skipped: {}", e.getMessage());
        }
    }


    private boolean isJobRunning(String jobName) {
        return !jobExplorer.findRunningJobExecutions(jobName).isEmpty();
    }
}
