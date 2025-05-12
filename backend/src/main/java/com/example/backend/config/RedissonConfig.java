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

    @Value("${spring.redis.password}")
    private String redisPassword;

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

        // nettyThreads 는 Config 에 설정해야 합니다 (SingleServerConfig 에는 없음)
        int nettyThreads = Math.max(poolSize / 2, Runtime.getRuntime().availableProcessors() * 2);
        config.setNettyThreads(nettyThreads);


        // 단일 서버 모드 → 클러스터 모드로만 변경
        config.useClusterServers()
                .addNodeAddress(String.format("redis://%s:%d", redisHost, redisPort))
                .setPassword(redisPassword)
                .setScanInterval(2000);
        
        /*
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
        */
        return Redisson.create(config);
    }
}
