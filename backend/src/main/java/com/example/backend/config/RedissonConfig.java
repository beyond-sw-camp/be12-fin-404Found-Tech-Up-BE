package com.example.backend.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.0.40.206:6379")
                .setTimeout(3000)
                .setConnectTimeout(5000)
                .setPingConnectionInterval(1000)  // 연결마다 ping 간격 설정
                .setConnectionMinimumIdleSize(2)
                .setConnectionPoolSize(10)
                .setRetryAttempts(2)
                .setRetryInterval(1500);
        return Redisson.create(config);
    }

}
