package com.example.backend.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.Duration;

@Configuration
public class RedissonConfig {


    private final Environment env;

    public RedissonConfig(Environment env) {
        this.env = env;
    }


    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.timeout}")
    private Duration timeout;

    @Value("${spring.redis.connect-timeout}")
    private Duration connectTimeout;

    @Value("${spring.redis.lettuce.pool.max-active}")
    private int poolSize;

    @Value("${spring.redis.lettuce.pool.min-idle}")
    private int minIdle;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        try {
            String[] nodeAddresses = env.getProperty("clusterServersConfig.nodeAddresses", String[].class);

            if (nodeAddresses == null || nodeAddresses.length == 0) {
                System.err.println("⚠️ spring.redis.cluster.nodes 설정이 없어서 Redis 연결을 생략합니다.");
                return null;
            }
            Config config = new Config();
            config.setNettyThreads(32);
            config.useClusterServers()
                    .addNodeAddress( nodeAddresses )
                    .setPassword(redisPassword.isEmpty() ? null : redisPassword)
                    .setScanInterval(2000);;

            return Redisson.create(config);
        } catch (Exception e) {
            System.err.println("⚠️ Redisson 연결 실패: " + e.getMessage());
            return null;
        }
    }
}