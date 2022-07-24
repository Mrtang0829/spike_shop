package com.tz.spike_shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class SpikeResultThreadPool {

    @Autowired
    private SpikeResultThreadPoolConfig config;

    @Bean("spikePool")
    public Executor spikePool() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(config.getCorePoolSize());
        pool.setMaxPoolSize(config.getMaxPoolSize());
        pool.setKeepAliveSeconds(config.getKeepAliveSeconds());
        pool.setQueueCapacity(config.getQueueCapacity());
        pool.setThreadNamePrefix("spike-result-websocket-");
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.initialize();
        return pool;
    }

}
