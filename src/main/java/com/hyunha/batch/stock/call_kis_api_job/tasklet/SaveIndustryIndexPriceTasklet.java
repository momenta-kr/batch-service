package com.hyunha.batch.stock.call_kis_api_job.tasklet;

import com.hyunha.batch.stock.call_kis_api_job.enums.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SaveIndustryIndexPriceTasklet implements Tasklet {

    private final StringRedisTemplate stringRedisTemplate;

    @Nullable
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Object v = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("industryIndexPrice");

        if (v == null) {
            log.error("[REDIS] industryIndexPrice not found in JobExecutionContext. skip");
            return RepeatStatus.FINISHED;
        }

        String json = String.valueOf(v);
        stringRedisTemplate
                .opsForValue()
                .set(
                        RedisKey.INDUSTRY_INDEX_PRICE.getKey(),
                        json,
                        RedisKey.INDUSTRY_INDEX_PRICE.getTtl()
                );
        return RepeatStatus.FINISHED;
    }
}
