package com.mogakko.be_final.scheduler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.Executor;

@Configuration
public class SchedulerConfig implements AsyncConfigurer, SchedulingConfigurer {

    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        scheduler.setThreadNamePrefix("OPENVIDU-SCHEDULER-");
        scheduler.initialize();
        Instant.now(Clock.system(ZoneId.of("Asia/Seoul")));
        return scheduler;
    }

    @Override
    public Executor getAsyncExecutor() {
        return this.threadPoolTaskScheduler();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(this.threadPoolTaskScheduler());
    }
}
