package com.hyunha.batch.stock.call_kis_api_job.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // ✅ 스케줄 메서드가 동시에 많이 뜰 수 있으니 넉넉히
        scheduler.setPoolSize(8);

        // 쓰레드 이름으로 로그에서 구분 쉬움
        scheduler.setThreadNamePrefix("sched-");

        // 스케줄 실행 중 예외 로깅
        scheduler.setErrorHandler(t -> log.error("[Scheduler] Unhandled exception", t));

        // 종료 시 처리
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);

        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }
}
