package com.hyunha.batch.stock.call_kis_api_job.scheduler;

import com.hyunha.batch.stock.call_kis_api_job.application.NaverNewsService;
import com.hyunha.batch.stock.call_kis_api_job.application.ScrapyService;
import com.hyunha.batch.stock.call_kis_api_job.domain.ScrapydJob;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.StockMasterRepository;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.StockTierDailyRepository;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.*;
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

import java.time.LocalDate;
import java.util.List;

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
    private final Job saveSearchDefaultJob;

    private final StockTierDailyRepository stockTierDailyRepository;
    private final StockMasterRepository stockMasterRepository;
    private final NaverNewsService naverNewsService;


    @Scheduled(fixedDelay = 300_000)
    public void runSaveSearchDefaultJob() {
        if (isJobRunning("saveSearchDefaultJob")) {
            log.info("Job already running, skip");
            return;
        }

        JobParameters params = new JobParametersBuilder()
                .addLong("runAt", System.currentTimeMillis()) // ⭐ 중요
                .toJobParameters();

        try {
            jobLauncher.run(saveSearchDefaultJob, params);
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.warn("Batch job skipped: {}", e.getMessage());
        }
    }

    /**
     * 뉴스기사를 정해진 시간마다 보여주기 위해서 KOSPI 종목에 tier를 부여함
     */
    @Scheduled(cron = "0 30 19 * * *", zone = "Asia/Seoul")
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

    @Scheduled(fixedDelay = 300_000)
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

    @Scheduled(fixedDelay = 300_000)
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

    @Scheduled(fixedDelay = 300_000)
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


    // 08:00 ~ 15:55 (5분마다)
    @Scheduled(cron = "0 */5 8-23 * * *", zone = "Asia/Seoul")
    public void runTier0Stocks() {
        runStockNews(Tier.T0);
    }

    private void runStockNews(Tier tier) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<StockTierDaily> stockTierDailies = stockTierDailyRepository.findByAsOfDateAndTierOrderByScore(yesterday, tier);
        List<StockMasterId> stockMasterIds = stockTierDailies.stream().map(std -> new StockMasterId(StockMarket.KOSPI, std.getSymbol())).toList();
        List<Stock> stocks = stockMasterRepository.findByIdIn(stockMasterIds);
        naverNewsService.fetchNews(stocks);
        log.info("runStockNews tier={}, count={}", tier, stocks.size());
    }

    // 08:00 ~ 15:55 (15분마다)
    @Scheduled(cron = "0 */15 8-15 * * *", zone = "Asia/Seoul")
    public void runTier1Stocks() {
        runStockNews(Tier.T1);
    }

    // 08:00 ~ 15:55 (60분마다)
    @Scheduled(cron = "0 0 8-15 * * *", zone = "Asia/Seoul")
    public void runTier2Stocks() {
        runStockNews(Tier.T2);
    }

    // 08:00에 한번
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    public void runTier3Stocks() {
        runStockNews(Tier.T3);
    }

    private boolean isJobRunning(String jobName) {
        return !jobExplorer.findRunningJobExecutions(jobName).isEmpty();
    }
}
