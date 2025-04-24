package com.togedog.scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);  // 최소 스레드 수
        executor.setMaxPoolSize(5);  // 최대 스레드 수
        executor.setQueueCapacity(100);  // 큐에 들어갈 작업의 최대 수
        executor.setThreadNamePrefix("AsyncScheduler-");
        executor.initialize();
        return executor;
    }
}

