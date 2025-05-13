package com.example.backend.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.timeout}")
    private Duration timeout;

    @Value("${spring.redis.connect-timeout}")
    private Duration connectTimeout;

    @Value("${spring.redis.lettuce.pool.max-active}")
    private int poolSize;

    @Value("${spring.redis.lettuce.pool.min-idle}")
    private int minIdle;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        int nettyThreads = Math.max(poolSize / 2, Runtime.getRuntime().availableProcessors() * 2);
        config.setNettyThreads(nettyThreads);

        String address = String.format("redis://%s:%d", redisHost, redisPort);
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(address)
                .setTimeout((int) timeout.toMillis())
                .setConnectTimeout((int) connectTimeout.toMillis())
                .setConnectionPoolSize(poolSize)
                .setConnectionMinimumIdleSize(minIdle)
                .setPingConnectionInterval(1_000)
                .setRetryAttempts(2)
                .setRetryInterval(1_500);

        return Redisson.create(config);
    }
}