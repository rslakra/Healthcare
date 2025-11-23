package com.rslakra.healthcare.routinecheckup.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for async operations, particularly email sending
 * 
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Configure a dedicated thread pool for async email operations
     * This prevents email sending from blocking the main request threads
     */
    @Bean(name = "emailTaskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Core pool size - minimum threads always available
        executor.setCorePoolSize(10);
        // Maximum pool size - can grow to handle bursts
        executor.setMaxPoolSize(50);
        // Queue capacity - buffer for tasks when all threads are busy
        executor.setQueueCapacity(100);
        // Thread name prefix for easier debugging
        executor.setThreadNamePrefix("email-async-");
        // Keep alive time for idle threads beyond core pool size
        executor.setKeepAliveSeconds(60);
        // Reject policy - if queue is full, run in caller's thread (shouldn't happen with proper sizing)
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        
        log.info("Async email executor configured: corePoolSize={}, maxPoolSize={}, queueCapacity={}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
}

