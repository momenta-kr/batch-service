package com.hyunha.batch.stock.call_kis_api_job.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveTop20ToRedisTasklet implements Tasklet {

    private final StringRedisTemplate stringRedisTemplate;

    // 원하는 키 네이밍 (거래소/전략/버전별로 분리 추천)
    private static final String KEY = "stock:momentum:top20:NAS";
    private static final Duration TTL = Duration.ofSeconds(15); // 예: 15초 (스케줄 주기에 맞춰 조정)

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        Object v = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("top20");

        if (v == null) {
            log.warn("[REDIS] top20 not found in JobExecutionContext. skip");
            return RepeatStatus.FINISHED;
        }

        String json = String.valueOf(v);
        stringRedisTemplate.opsForValue().set(KEY, json, TTL);
        return RepeatStatus.FINISHED;
    }
}
