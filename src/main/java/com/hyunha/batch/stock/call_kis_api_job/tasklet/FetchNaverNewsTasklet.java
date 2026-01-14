package com.hyunha.batch.stock.call_kis_api_job.tasklet;

import com.hyunha.batch.stock.call_kis_api_job.application.NaverNewsService;
import com.hyunha.batch.stock.call_kis_api_job.application.ScrapyService;
import com.hyunha.batch.stock.call_kis_api_job.domain.ScrapydJob;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.StockMasterRepository;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class FetchNaverNewsTasklet implements Tasklet {

    private final StockMasterRepository stockMasterRepository;
    private final NaverNewsService naverNewsService;
    private final ScrapyService scrapyService;


    @Nullable
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        List<Stock> stocks = stockMasterRepository.findAllKospi();
        log.info("stockMasters size={}", stocks.size());

        List<ScrapydJob> dedupedAllJobs = naverNewsService.fetchNews(stocks);
        scrapyService.callScrapy(dedupedAllJobs);
        return RepeatStatus.FINISHED;
    }


}
