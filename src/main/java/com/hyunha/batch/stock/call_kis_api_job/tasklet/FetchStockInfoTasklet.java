package com.hyunha.batch.stock.call_kis_api_job.tasklet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunha.batch.stock.call_kis_api_job.client.KisClient;
import com.hyunha.batch.stock.call_kis_api_job.enums.RedisKey;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.StockMasterRepository;
import com.hyunha.batch.stock.call_kis_api_job.model.response.DomesticStockCurrentPriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@RequiredArgsConstructor
@Component
public class FetchStockInfoTasklet implements Tasklet {

    private final StockMasterRepository stockMasterRepository;
    private final KisClient kisClient;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Nullable
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        stockMasterRepository.findAll().forEach(stockMaster -> {
            String stockCode = stockMaster.getId().getSymbol();
            DomesticStockCurrentPriceResponse domesticStockCurrentPriceResponse = kisClient.fetchDomesticStockCurrentPrice(stockCode);
            String redisKey = RedisKey.STOCK_INFO.getKey() + ":" + stockCode;
            String json = null;
            try {
                json = objectMapper.writeValueAsString(domesticStockCurrentPriceResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            stringRedisTemplate
                    .opsForValue()
                    .set(
                            redisKey,
                            json,
                            RedisKey.STOCK_INFO.getTtl()
                    );
        });

        return RepeatStatus.FINISHED;
    }
}
