package com.example.backend.config;

import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {
    /*
    @Value("${redis.cluster.nodes}")
    private String redisNodes;

    @Value("${redis.password:}")
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

     */
    private final RedisClusterProperties redisProps;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        ClusterServersConfig cluster = config.useClusterServers();

        for (String node : redisProps.getNodes()) {
            cluster.addNodeAddress("redis://" + node);
        }
        cluster.setNatMap(Map.of(
                "172.28.0.10:7000", "127.0.0.1:17000",
                "172.28.0.11:7001", "127.0.0.1:17001",
                "172.28.0.12:7002", "127.0.0.1:17002",
                "172.28.0.13:7003", "127.0.0.1:17003",
                "172.28.0.14:7004", "127.0.0.1:17004",
                "172.28.0.15:7005", "127.0.0.1:17005"
        ));
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
