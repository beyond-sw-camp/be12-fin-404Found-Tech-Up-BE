package com.example.backend.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.cluster.nodes}")
    private List<String> clusterNodes;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        config.setNettyThreads(Runtime.getRuntime().availableProcessors() * 2);

        config.useClusterServers()
                .addNodeAddress(clusterNodes.stream()
                        .map(node -> node.startsWith("redis://") ? node : "redis://" + node)
                        .toArray(String[]::new))
                .setPassword(redisPassword)
                .setScanInterval(2000);

        return Redisson.create(config);
    }
}