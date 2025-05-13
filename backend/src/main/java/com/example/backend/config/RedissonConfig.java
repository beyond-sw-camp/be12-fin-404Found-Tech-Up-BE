package com.example.backend.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.cluster.nodes}")
    private String redisNodes;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        ClusterServersConfig cluster = config.useClusterServers();

        for (String node : redisNodes.split(",")) {
            cluster.addNodeAddress("redis://" + node.trim());
        }

        if (!redisPassword.isBlank()) {
            cluster.setPassword(redisPassword);
        }

        cluster.setScanInterval(2000);
        config.setNettyThreads(32);

        return Redisson.create(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}