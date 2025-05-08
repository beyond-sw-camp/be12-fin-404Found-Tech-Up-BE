package com.example.backend.coupon.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public Boolean sIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Long sCard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    public Long rPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public Long sAdd(String key, String value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    public void flushAll() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .flushAll();
    }

    public Long hIncrBy(String key, String field, long delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }


}
